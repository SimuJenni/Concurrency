package assignment2;

import assignment1.Counter;

public class SimpleCounter implements Counter {

	private long counter;
	
	public SimpleCounter() {
		counter = 0;
	}

	@Override
	public void increment() {
		counter++;
	}

	@Override
	public void decrement() {
		counter--;
	}

	@Override
	public long getCounter() {
		return counter;
	}

}
