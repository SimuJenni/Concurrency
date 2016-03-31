package assignment2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PetersonLock implements Lock {
	
	final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>(){
        final private AtomicInteger id = new AtomicInteger(0);

        protected Integer initialValue(){
            return id.getAndIncrement();
        }
    };
	
	private AtomicInteger[] level;
	private AtomicInteger[] victim;
	private int numThreads;

	public PetersonLock(int n) {
		level = new AtomicInteger[n];
		victim = new AtomicInteger[n];
		for (int i = 0; i < n; i++) {
			level[i] = new AtomicInteger();
			victim[i] = new AtomicInteger();
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
			victim[L].set(threadID);
			while (existsHigher(threadID, L) && (victim[L].get() == threadID)) {
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
		level[threadID].set(0);
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
