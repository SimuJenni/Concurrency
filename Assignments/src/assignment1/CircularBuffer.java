package assignment1;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class CircularBuffer {
	private int[] buffer;
	private int in, out;
	private int buffSize;
	private Semaphore consumeSem;
	private Semaphore produceSem;
	private Semaphore lockBuff;


	public CircularBuffer(int capacity) {
		buffer = new int[capacity];
		buffSize = capacity;
		// Initial semaphore value of 0!
		consumeSem = new Semaphore(0);
		produceSem = new Semaphore(capacity);
		lockBuff = new Semaphore(1);
		in = 0;
		out = 0;
	}

	public void produce(int value) throws InterruptedException {
		// Decrease produceSem
		produceSem.acquire();
		// Enter element and advance in
		lockBuff.acquire();
		buffer[in] = value;
		in = (in + 1) % buffSize;
		lockBuff.release();
		// Increase consumeSem
		consumeSem.release();
	}

	public int consume() throws InterruptedException {
		// Decrease the semaphore
		consumeSem.acquire();
		// Consume and advance out
		lockBuff.acquire();
		int result = buffer[out];
		buffer[out] = 0;
		out = (out + 1) % buffSize;
		lockBuff.release();
		// Increase produceSem
		produceSem.release();
		return result;
	}

	@Override
	public String toString() {
		return "Buffer state: " + Arrays.toString(buffer);
	}

}
