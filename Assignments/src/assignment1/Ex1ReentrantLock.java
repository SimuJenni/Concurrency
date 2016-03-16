package assignment1;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1ReentrantLock {

	public static void main(String[] args) {
		if (args.length == 3) {
			// Parse the inputs
			int n = Integer.parseInt(args[0]);
			int m = Integer.parseInt(args[1]);
			int i = Integer.parseInt(args[2]);

			SharedCounterLock counter = new SharedCounterLock();
			ExecutorService executor = Executors.newFixedThreadPool(n + m);

			// Threads of type 1
			Runnable task1 = new Runnable() {
				public void run() {
					for (int k = 0; k < i; k++) {
						counter.increment();
					}
				}
			};

			// Threads of type 2
			Runnable task2 = new Runnable() {
				public void run() {
					for (int k = 0; k < i; k++) {
						counter.decrement();
					}
				}
			};

			// Run the threads
			for (int j = 0; j < n; j++) {
				executor.execute(task1);
			}

			for (int j = 0; j < m; j++) {
				executor.execute(task2);
			}

			// Wait till all threads have stopped and output final counter value
			executor.shutdown();
			try {
				boolean finshed = executor.awaitTermination(1, TimeUnit.MINUTES);
				if (finshed) {
					System.out.println("Counter at the end of Execution: " + counter.counter);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Expected three integers as input!");
		}
	}
}

class SharedCounterLock {

	ReentrantLock lock = new ReentrantLock();
	int counter = 0;

	void decrement() {
		lock.lock();
		try {
			counter--;
		} finally {
			lock.unlock();
		}
	}

	void increment() {
		lock.lock();
		try {
			counter++;
		} finally {
			lock.unlock();
		}
	}
}