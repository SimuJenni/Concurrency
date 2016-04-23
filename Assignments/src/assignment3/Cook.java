package assignment3;

public class Cook implements Runnable {

	private Pot pot;
	private boolean shouldRefill;

	public Cook(Pot p) {
		pot = p;
		shouldRefill = false;
		p.cook = this;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			if (shouldRefill) {
				pot.refill();
				shouldRefill = false;
			}
		}
	}

	public void refillPot() {
		shouldRefill = true;
	}

}