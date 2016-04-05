package assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex2 {

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 2) {
			// Parse the inputs
			int numThreads = Integer.parseInt(args[0]);
			int buffSize = Integer.parseInt(args[1]);

			ExecutorService executor = Executors.newFixedThreadPool(2 * numThreads);
			CircularBuffer sharedBuffer = new CircularBuffer(buffSize);

			// Start threads
			for (int j = 0; j < numThreads; j++) {
				ConsumeThread consumer = new ConsumeThread(sharedBuffer);
				ProduceThread producer = new ProduceThread(sharedBuffer);
				executor.execute(consumer);
				executor.execute(producer);
			}
			
			long startTime = System.nanoTime();

			// Wait till all threads have stopped and output final counter value
			executor.shutdown();
			boolean finshed = executor.awaitTermination(10, TimeUnit.SECONDS);
			if (finshed) {
				long runtime = System.nanoTime() - startTime;
				System.out.println("Runtime: " + runtime * 0.000001);
				// Print out the buffer to check if all elements are zero as
				// expected
				System.out.println("Final Buffer: " + sharedBuffer);
			}

		} else {
			System.out.println("Expected two integers as input!");
		}
	}

}

class ProduceThread implements Runnable {

	private CircularBuffer buffer;

	public ProduceThread(CircularBuffer sharedBuffer) {
		buffer = sharedBuffer;
	}

	public void run() {
		int randInt = (int) (Math.random() * 1000) + 1;
		try {
			buffer.produce(randInt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ConsumeThread implements Runnable {

	private CircularBuffer buffer;
	private int consumed;

	public ConsumeThread(CircularBuffer sharedBuffer) {
		buffer = sharedBuffer;
	}

	public void run() {
		try {
			consumed = buffer.consume();
			if (consumed == 0) {
				// To make sure we do not consume too much
				throw new Exception("Error: Consumed a 0!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getConsumed() {
		return consumed;
	}

}
