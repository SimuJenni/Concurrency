package assignment4;

public class FineGrainedList<T> implements Set<T> {

	private Node<T> head, tail; // The sentinel nodes

	public FineGrainedList() {
		super();
		head = new Node<T>(Integer.MIN_VALUE);
		tail = new Node<T>(Integer.MAX_VALUE);
		head.setNext(tail);
	}

	@Override
	public boolean add(T x) {
		int key = x.hashCode();
		Node<T> curr = this.head;
		Node<T> pred = null;
		try {
			pred = this.head;
			pred.lock();
			curr = pred.getNext();
			curr.lock();
			// Traverse list till the correct position
			while (curr.getKey() < key) {
				pred.unlock();
				pred = curr;
				curr = curr.getNext();
				curr.lock();
			}
	        if (curr.getKey() == key) {
	            return false;
	        }
			// Create new node and add...
			Node<T> node2add = new Node<T>(x);
			node2add.setNext(curr);
			pred.setNext(node2add);
			return true;
		} finally {
			curr.unlock();
			pred.unlock();
		}
	}

	@Override
	public boolean remove(T x) {
		int key = x.hashCode();
		Node<T> curr = this.head;
		Node<T> pred = null;
		try {
			pred = this.head;
			pred.lock();
			curr = pred.getNext();
			curr.lock();
			// Traverse list till the correct position
			while (curr.getKey() <= key) {
				if (x == curr.getObject()) {
					// Remove...
					pred.setNext(curr.getNext());
					return true;
				}
				pred.unlock();
				pred = curr;
				curr = curr.getNext();
				curr.lock();
			}
			return false;
		} finally {
			curr.unlock();
			pred.unlock();
		}
	}

	@Override
	public boolean contains(T x) {
		int key = x.hashCode();
		Node<T> curr = this.head;
		Node<T> pred = null;
		try {
			pred = this.head;
			pred.lock();
			curr = pred.getNext();
			curr.lock();
			// Traverse list till the correct position
			while (curr.getKey() <= key) {
				if (x == curr.getObject()) {
					// Found...
					return true;
				}
				pred.unlock();
				pred = curr;
				curr = curr.getNext();
				curr.lock();
			}
			// Not found...
			return false;
		} finally {
			curr.unlock();
			pred.unlock();
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer("List: [");
		Node<T> curr = head;
		// Traverse list till the correct position
		while (curr != tail) {
			stringBuffer.append(curr.getObject());
			stringBuffer.append(", ");
			curr = curr.getNext();
		}
		stringBuffer.append(curr.getObject());
		stringBuffer.append(" ]");
		return stringBuffer.toString();
	}

}
