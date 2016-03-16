package assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex1Sync {

	public static void main(String[] args) {
		if (args.length == 3) {
			// Parse the inputs
			int n = Integer.parseInt(args[0]);
			int m = Integer.parseInt(args[1]);
			int i = Integer.parseInt(args[2]);

			SharedCounterSync counter = new SharedCounterSync();
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

class SharedCounterSync {
	int counter = 0;

	synchronized void decrement() {
		counter--;
	}

	synchronized void increment() {
		counter++;
	}
}
