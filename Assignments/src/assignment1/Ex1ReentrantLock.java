package assignment1;

import java.util.concurrent.locks.ReentrantLock;

public class Ex1ReentrantLock {

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 3) {
			// Parse the inputs
			int n = Integer.parseInt(args[0]);
			int m = Integer.parseInt(args[1]);
			int numItr = Integer.parseInt(args[2]);
			
			Ex1Runner runner = new Ex1Runner(new SharedCounterLock());
			runner.startAndRun(n, m, numItr);

		} else {
			System.out.println("Expected three integers as input!");
		}
	}
}

class SharedCounterLock implements Counter {

	ReentrantLock lock = new ReentrantLock();
	int counter = 0;

	public void decrement() {
		lock.lock();
		try {
			counter--;
		} finally {
			lock.unlock();
		}
	}

	public void increment() {
		lock.lock();
		try {
			counter++;
		} finally {
			lock.unlock();
		}
	}

	public int getCounter() {
		return counter;
	}
}