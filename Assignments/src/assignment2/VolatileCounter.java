package assignment2;

import assignment1.Counter;

public class VolatileCounter implements Counter {
	
	private volatile long counter;
		
	public VolatileCounter() {
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
