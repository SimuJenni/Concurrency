package assignment2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PetersonLockFair implements Lock {
	/*
	 * The idea here is to have a counter at each level working like a ticket
	 * machine: 
	 * - When a thread is at the doorway of level L it takes a ticket
	 * of the level L counter and increments the counter (atomically!). 
	 * - Each level has a second counter which keeps track of the next ticket 
	 * that can pass the doorway. This counter is incremented by the thread which
	 * last left the level and is now waiting at the doorway of the next level (or
	 * during unlocking). 
	 * - The test to go to the next level then consists of
	 * checking whether there is a thread in the same or higher level and if the
	 * ticket of this thread is next to pass.
	 */

	final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>() {
		final private AtomicInteger id = new AtomicInteger(0);

		protected Integer initialValue() {
			return id.getAndIncrement();
		}
	};

	private AtomicInteger[] level;
	private AtomicInteger[] ticket;
	private AtomicInteger[] next;
	private int numThreads;

	public PetersonLockFair(int n) {
		level = new AtomicInteger[n];
		ticket = new AtomicInteger[n];
		next = new AtomicInteger[n];
		for (int i = 0; i < n; i++) {
			level[i] = new AtomicInteger();
			ticket[i] = new AtomicInteger(0);
			next[i] = new AtomicInteger(0);
		}
		numThreads = n;
	}

	private boolean existsHigher(int threadId, int threadLvl) {
		for (int i = 0; i < numThreads; i++) {
			if (i == threadId) {
				continue;
			} else {
				if (level[i].get() >= threadLvl) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void lock() {
		int threadID = THREAD_ID.get();
		for (int L = 1; L < numThreads; L++) {
			level[threadID].set(L);
			int myTicket = ticket[L].getAndIncrement();
			next[L - 1].incrementAndGet(); // Only increment when already in new
											// level!

			// // For verification and debugging
			// System.out.println("Thread "+threadID+" got ticket "+myTicket+"
			// at lvl "+L);

			while (existsHigher(threadID, L) && !(next[L].get() == myTicket)) {
				// Its either not my turn or somebody is higher than me
			}

		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		int threadID = THREAD_ID.get();

		// // For verification and debugging
		// System.out.println("Thread "+threadID+" unlocked!");

		level[threadID].set(0);
		next[numThreads - 1].incrementAndGet();
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
