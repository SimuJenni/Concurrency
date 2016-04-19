package assignment2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import assignment1.Counter;

public class Ex1 {

	public static void main(String[] args) throws InterruptedException {
		/*
		 * Runtime arguments: (int numThreads, int counterGoal, boolean
		 * volatile, boolean solarisAffinity, int numRuns)
		 * 
		 */

		int numThreads = 0, counterGoal = 0, runs = 1;
		boolean vol = false, solarisAffinity = false;
		try {
			numThreads = Integer.parseInt(args[0]);
			counterGoal = Integer.parseInt(args[1]);
			if (!args[2].equalsIgnoreCase("true")&&!args[2].equalsIgnoreCase("false")) {
				throw(new Exception("Expected \"false\" or \"true\" as third argument!" ));
			}
			vol = Boolean.parseBoolean(args[2]);
			if (!args[3].equalsIgnoreCase("true")&&!args[3].equalsIgnoreCase("false")) {
				throw(new Exception("Expected \"false\" or \"true\" as 4th argument!" ));
			}
			solarisAffinity = Boolean.parseBoolean(args[3]);
			runs = 1;
			if (args.length > 4) {
				runs = Integer.parseInt(args[4]);
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("ERROR parsing input! Expected input of to be of the form: ");
			System.out.println(
					"(int numThreads, int counterGoal, boolean volatile,  boolean solarisAffinity, int numRuns)");
			return;
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
			// PetersonLockFair lock = new PetersonLockFair(numThreads);	// My fancy fair lock :)

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
				IncrementerThread thread = new IncrementerThread(sharedCounter, lock, counterGoal, accesses);
				executor.execute(thread);
			}

			long startTime = System.nanoTime();

			// Wait till all threads have stopped and output final counter value
			executor.shutdown();
			boolean finshed = executor.awaitTermination(5, TimeUnit.MINUTES);
			if (finshed) {
				runtime += System.nanoTime() - startTime;
				finalCounter += sharedCounter.getCounter();
				// Gather statistics: Accumulate over all runs
				int maxAcc = 0;
				int minAcc = counterGoal;
				for (int i = 0; i < accesses.length; i++) {
					minAcc = accesses[i] < minAcc ? accesses[i] : minAcc;
					maxAcc = accesses[i] > maxAcc ? accesses[i] : maxAcc;
				}
				sumMins += minAcc;
				sumMaxs += maxAcc;
			}

		}

		// Print average statistics
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