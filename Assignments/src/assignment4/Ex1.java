package assignment4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import assignment1.Counter;
import assignment2.PetersonLock;
import assignment2.VolatileCounter;

public class Ex1 {
	public static void main(String[] args) throws InterruptedException {
	/*
	 * Runtime arguments: (boolean useCAS, int numThreads, int counterGoal, int numRuns)
	 */

	int numThreads = 0, counterGoal = 0, runs = 1;
	boolean useCAS = false;
	try {
		if (!args[0].equalsIgnoreCase("true") && !args[0].equalsIgnoreCase("false")) {
			throw (new Exception("Expected \"false\" or \"true\" as first argument!"));
		}
		useCAS = Boolean.parseBoolean(args[0]);
		numThreads = Integer.parseInt(args[1]);
		counterGoal = Integer.parseInt(args[2]);
		runs = 1;
		if (args.length > 3) {
			runs = Integer.parseInt(args[3]);
		}
	} catch (Exception e) {
		System.out.println(e);
		System.out.println("ERROR parsing input! Expected input of to be of the form: ");
		System.out.println(
				"(int numThreads, int counterGoal, int numRuns)");
		return;
	}

	long runtime = 0;
	int finalCounter = 0;
	int sumMins = 0;
	int sumMaxs = 0;

	System.out.println("Number of Threads: " + numThreads);

	for (int r = 0; r < runs; r++) {
		Lock lock;
		if (useCAS) {
			lock = new CASLock();
		} else {
			lock = new PetersonLock(numThreads);
		}

		Counter sharedCounter = new VolatileCounter();		

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
	if (useCAS) {
		System.out.println("Lock-Type: CAS");
	} else {
		System.out.println("Lock-Type: Reentrant");
	}
	System.out.println("Number of Runs: " + runs);
	System.out.println("Average Runtime [ms]: " + runtime / runs * 0.000001);
	System.out.println("Final Counter: " + finalCounter / runs);
	System.out.println("Minimum #Accesses: " + sumMins / runs);
	System.out.println("Maximum #Accesses: " + sumMaxs / runs);
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


