package assignment4;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {
	private T object;
	private int key;
	private Node<T> next;
	private Lock lock;

	public Node(T object) {
		super();
		this.object = object;
		this.key = object.hashCode();
		this.lock = new CASLock();
	}
	
	public Node(int key) {
		super();
		this.object = null;
		this.key = key;
		this.lock = new ReentrantLock();
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Node<T> getNext() {
		return next;
	}

	public void setNext(Node<T> next) {
		this.next = next;
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}
