package assignment3;

import java.util.HashSet;

public class Pot {
	/*
	 * Represent the pot by an array of booleans where each element represents
	 * whether the corresponding meal has been consumed.
	 */

	private volatile Boolean[] pot;
	private volatile boolean empty = true;
	private int numSavages;
	private volatile HashSet<Long> eaten; // A set indicating which threads have
											// eaten
	Cook cook;

	public Pot(int potSize, int nSavages) {
		numSavages = nSavages;
		eaten = new HashSet<Long>();
		pot = new Boolean[potSize];
	}

	public boolean consume(long threadID) {
		/*
		 * If there is a meal in the pot, this method will consume it and return
		 * true. Otherwise it returns false.
		 */
		while (eaten.contains(threadID) && eaten.size() < numSavages) {
			// This savage has already eaten and there are others that haven't
			// yet
		}
		for (int i = 0; i < pot.length; i++) {
			int idx = (int) ((i + threadID) % pot.length); // Don't make every
															// thread start at
															// the same position
			synchronized (pot[idx]) { // Need only lock this entry
				if (pot[idx]) {
					pot[idx] = false; // Consume..
					System.out.println("Savage " + threadID + " eats like a pig!");
					eaten.add(threadID);
					if (eaten.size() == numSavages) {
						eaten = new HashSet<Long>(); // If all have eaten, allow
														// anyone to grab some
														// more
					}
					return true;
				}
			}
		}
		empty = true;
		return false;
	}

	public void orderRefill() {
		cook.refillPot();
	}

	public synchronized boolean refill() {
		/*
		 * Fills up the pot. First checks if the pot is empty and returns false
		 * f it isn't. Otherwise fills the pot and returns true.
		 */
		if (isEmpty()) {
			System.out.println("Cook is refilling the pot!");
			for (int i = 0; i < pot.length; i++) {
				pot[i] = true;
			}
			empty = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean isEmpty() {
		return empty;
	}

}
