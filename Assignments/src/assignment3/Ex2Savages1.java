package assignment3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex2Savages1 {

	public static void main(String[] args) throws InterruptedException {

		// Parse input...
		int numSavages = 0, potSize = 0;
		try {
			numSavages = Integer.parseInt(args[0]);
			potSize = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("ERROR parsing input! Expected input of to be of the form: ");
			System.out.println("(int numSavages, int potSize)");
			return;
		}

		Pot sharedPot = new Pot(potSize, numSavages);

		ExecutorService executor = Executors.newFixedThreadPool(numSavages + 1);

		// Start threads
		Cook cook = new Cook(sharedPot);
		executor.execute(cook);
		for (int i = 0; i < numSavages; i++) {
			Savage savage = new Savage(sharedPot);
			executor.execute(savage);
		}

		// Wait till all threads have stopped and output final counter value
		executor.shutdown();
		if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
			System.out.println("task completed");
		} else {
			System.out.println("Forcing shutdown...");
			executor.shutdownNow();
		}
		return;
	}
}

class Savage implements Runnable {

	private Pot pot;
	private boolean hungry;

	public Savage(Pot p) {
		hungry = true;
		pot = p;
	}

	@Override
	public void run() {
		while (hungry && !Thread.currentThread().isInterrupted()) {
			if (pot.isEmpty()) {
				pot.orderRefill();
			} else {
				hungry = !pot.consume(Thread.currentThread().getId());
			}
		}
	}

}
