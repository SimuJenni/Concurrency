package assignment3;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dining {

	public static void main(String[] args) throws InterruptedException {
		// Parse input...
		int numPhilosophers = 0;
		try {
			numPhilosophers = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("ERROR parsing input! Expected input of to be of the form: ");
			System.out.println("(int numPhilosophers)");
			return;
		}

		Fork[] forks = new Fork[numPhilosophers];
		for (int i = 0; i < forks.length; i++) {
			forks[i] = new Fork();
		}

		ExecutorService executor = Executors.newFixedThreadPool(numPhilosophers);
		// Start threads
		for (int i = 0; i < numPhilosophers - 1; i++) {
			Philospher diner = new Philospher(forks[i], forks[i + 1], true);
			executor.execute(diner);
		}
		// Let last Philosopher start with grabbing right fork (avoids deadlock
		// when all grab left...)
		Philospher diner = new Philospher(forks[numPhilosophers - 1], forks[0], false);
		executor.execute(diner);

		// Wait till all threads have stopped
		executor.shutdown();
		if (executor.awaitTermination(2, TimeUnit.SECONDS)) {
			System.out.println("task completed");
		} else {
			System.out.println("Forcing shutdown...");
			executor.shutdownNow();
		}
		return;

	}
}

class Philospher implements Runnable {

	private Fork left, right;
	private boolean leftFirst; // Indicates whether to pick up left fork first
	private Random rand;

	public Philospher(Fork l, Fork r, boolean lFirst) {
		left = l;
		right = r;
		leftFirst = lFirst;
		rand = new Random();
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					if (leftFirst) {
						left.grab();
						right.grab();
					} else {
						right.grab();
						left.grab();
					}
					eat();
				} finally { // Always release forks...
					if (leftFirst) {
						left.release();
						right.release();
					} else {
						right.release();
						left.release();
					}
				}
				sleep();
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}

	}

	private void sleep() throws InterruptedException {
		System.out.println("Philosopher " + Thread.currentThread().getName() + " sleeps!");
		Thread.sleep(rand.nextInt(1000));
	}

	private void eat() throws InterruptedException {
		System.out.println("Philosopher " + Thread.currentThread().getName() + " eats!");
		Thread.sleep(rand.nextInt(1000));
	}

}

class Fork {

	private Lock lock;

	public Fork() {
		lock = new ReentrantLock();
	}

	public void grab() {
		lock.lock();
	}

	public void release() {
		lock.unlock();
	}
}