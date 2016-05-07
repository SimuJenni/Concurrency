package assignment4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CASLock implements Lock {
	
	AtomicInteger state;
	
	public CASLock() {
		this.state = new AtomicInteger(0);
	}

	@Override
	public void lock() {
		while (true) {
			while (state.get()!=0) {}
			if (state.compareAndSet(0, 1)) {
				return;
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
		state.set(0);
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
