package assignment4;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex2 {
	public static void main(String[] args) throws InterruptedException {
		/*
		 * Runtime arguments: (int numThreads, boolean useOptimisticList)
		 */

		int numThreads = 0;
		boolean useOptimisticList = false;

		try {
			numThreads = Integer.parseInt(args[0]);
			if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
				throw (new Exception("Expected \"false\" or \"true\" as first argument!"));
			}
			useOptimisticList = Boolean.parseBoolean(args[1]);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("ERROR parsing input! Expected input of to be of the form: ");
			System.out.println("(int numThreads)");
			return;
		}

		System.out.println("Number of Threads: " + numThreads);

		int numInts = 100000;
		int max2choose = 100;
		int[] randomInts = new int[numInts];
		for (int i = 0; i < randomInts.length; i++) {
			randomInts[i] = (int) (Math.random() * max2choose);
		}

		Set<Integer> sharedList;
		if (useOptimisticList) {
			sharedList = new OptimisticList<Integer>();
		} else {
			sharedList = new FineGrainedList<Integer>();
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);

		for (int i = 0; i < numThreads; i++) {
			int from = i * numInts / numThreads;
			int to = (i + 1) * numInts / numThreads;
			int[] threadInts = Arrays.copyOfRange(randomInts, from, to);
			Runnable thread;
			if (i % 2 == 0) {
				thread = new AdderThread(sharedList, threadInts);
			} else {
				thread = new RemoverThread(sharedList, threadInts);
			}
			executor.execute(thread);
		}
		long startTime = System.nanoTime();

		// Wait till all threads have stopped and output final counter value
		executor.shutdown();
		boolean finshed = executor.awaitTermination(5, TimeUnit.MINUTES);
		if (finshed) {
			long runtime = System.nanoTime() - startTime;
			System.out.println("Runtime [ms]: " + runtime * 0.000001);
			System.out.println("" + sharedList.toString());
		}
	}

}

class AdderThread implements Runnable {

	private int[] ints2add;
	private Set<Integer> list;

	public AdderThread(Set<Integer> sharedList, int[] ints2add) {
		this.ints2add = ints2add;
		this.list = sharedList;
	}

	public void run() {
		for (int i = 0; i < ints2add.length; i++) {
			list.add(ints2add[i]);
		}
	}
}

class RemoverThread implements Runnable {

	private int[] ints2remove;
	private Set<Integer> list;

	public RemoverThread(Set<Integer> sharedList, int[] ints2remove) {
		this.ints2remove = ints2remove;
		this.list = sharedList;
	}

	public void run() {
		for (int i = 0; i < ints2remove.length; i++) {
			list.remove(ints2remove[i]);
		}
	}
}
