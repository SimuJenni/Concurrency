package assignment2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import assignment1.Counter;

public class Ex1 {

	public static void main(String[] args) throws InterruptedException {
		// Runtime args: (int numThreads, int maxVal, boolean volatile, boolean
		// solarisAffinity)

		int numThreads = Integer.parseInt(args[0]);
		int maxVal = Integer.parseInt(args[1]);
		boolean vol = Boolean.parseBoolean(args[2]);
		boolean solarisAffinity = Boolean.parseBoolean(args[3]);
		int runs = 1;
		if (args.length > 4) {
			runs = Integer.parseInt(args[4]);
		}

		long runtime = 0;
		int finalCounter = 0;
		int sumMins = 0;
		int sumMaxs = 0;

		System.out.println("Number of Threads: " + numThreads);
		System.out.println("Volatile Counter: " + vol);
		System.out.println("Solaris Processor-Affininty: " + solarisAffinity);

		for (int r = 0; r < runs; r++) {

			PetersonLock lock = new PetersonLock(numThreads);
			Counter sharedCounter;

			if (vol) {
				sharedCounter = new VolatileCounter();
			} else {
				sharedCounter = new SimpleCounter();
			}

			if (solarisAffinity) {
				setSolarisAffinity();
			}

			ExecutorService executor = Executors.newFixedThreadPool(numThreads);

			int[] accesses = new int[numThreads];

			// Start threads
			for (int i = 0; i < numThreads; i++) {
				IncrementerThread thread = new IncrementerThread(sharedCounter, lock, maxVal, accesses);
				executor.execute(thread);
			}

			long startTime = System.nanoTime();
			
			// Wait till all threads have stopped and output final counter value
			executor.shutdown();
			boolean finshed = executor.awaitTermination(5, TimeUnit.MINUTES);
			if (finshed) {
				runtime += System.nanoTime() - startTime;
				finalCounter += sharedCounter.getCounter();
				// Gather stats
				int maxAcc = 0;
				int minAcc = maxVal;
				for (int i = 0; i < accesses.length; i++) {
					minAcc = accesses[i] < minAcc ? accesses[i] : minAcc;
					maxAcc = accesses[i] > maxAcc ? accesses[i] : maxAcc;
				}
				sumMins += minAcc;
				sumMaxs += maxAcc;
			}

		}

		// Print average stats
		System.out.println("Runtime [ms]: " + runtime / runs * 0.000001);
		System.out.println("Final Counter: " + finalCounter / runs);
		System.out.println("Minimum #Accesses: " + sumMins / runs);
		System.out.println("Maximum #Accesses: " + sumMaxs / runs);
	}

	public static void setSolarisAffinity() {
		try {
			// retrieve process id
			String pid_name = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			String[] pid_array = pid_name.split("@");
			int pid = Integer.parseInt(pid_array[0]);
			// random processor
			int processor = new java.util.Random().nextInt(32);
			// Set process affinity to one processor (on Solaris)
			Process p = Runtime.getRuntime().exec("/usr/sbin/pbind -b " + processor + " " + pid);
			p.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}

class IncrementerThread implements Runnable {

	private Counter counter;
	private Lock lock;
	private int stopValue;
	private int[] accesses;

	public IncrementerThread(Counter sharedCounter, Lock lock, int maxVal, int[] accesses) {
		counter = sharedCounter;
		this.lock = lock;
		stopValue = maxVal;
		this.accesses = accesses;
	}

	public void run() {
		while (true) {
			try {
				lock.lock();
				if (counter.getCounter() < stopValue) {
					counter.increment();
				} else {
					break;
				}
				accesses[(int) (Thread.currentThread().getId() % accesses.length)]++;
			} finally {
				lock.unlock();
			}
		}
	}
}