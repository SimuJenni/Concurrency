package assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex1Runner {
	
	private Counter counter;
	
	public Ex1Runner(Counter counter){
		this.counter = counter;
	}

	public void startAndRun(int n, int m, int numItr) throws InterruptedException{
		ExecutorService executor = Executors.newFixedThreadPool(n + m);

		long startTime = System.nanoTime();

		// Threads of type 1
		for (int j = 0; j < n; j++) {
			Thread1 task1 = new Thread1(counter, numItr);
			executor.execute(task1);
		}

		// Threads of type 2
		for (int j = 0; j < m; j++) {
			Thread2 task2 = new Thread2(counter, numItr);
			executor.execute(task2);
		}

		// Wait till all threads have stopped and output final counter value
		executor.shutdown();
		boolean finshed = executor.awaitTermination(10, TimeUnit.SECONDS);
		if (finshed) {
			long runtime = System.nanoTime() - startTime;
			System.out.println("Counter at the end of Execution: " + counter.getCounter());
			System.out.println("Runtime: " + runtime * 0.000001);
		}
	}
}


class Thread1 implements Runnable {
	Counter counter;
	int numItr;

	public Thread1(Counter counter, int numItr) {
		this.counter = counter;
		this.numItr = numItr;
	}

	public void run() {
		for (int i = 0; i < numItr; i++) {
			counter.increment();
		}
	}
}

class Thread2 implements Runnable {
	Counter counter;
	int numItr;

	public Thread2(Counter counter, int numItr) {
		this.counter = counter;
		this.numItr = numItr;
	}

	public void run() {
		for (int i = 0; i < numItr; i++) {
			counter.decrement();
		}
	}
}
