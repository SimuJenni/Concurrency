package assignment4;

public class OptimisticList<T> implements Set<T> {

	private Node<T> head, tail; // The sentinel nodes

	public OptimisticList() {
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
		while (true) {
			pred = this.head;
			curr = pred.getNext();
			// Traverse list till the correct position
			while (curr.getKey() < key) {
				pred = curr;
				curr = curr.getNext();
			}
			if (curr.getKey() == key) {
				return false;
			}
			try {
				// Lock
				pred.lock();
				curr.lock();
				// Validate
				if (validate(pred, curr)) {
					// Create new node and add...
					Node<T> node2add = new Node<T>(x);
					node2add.setNext(curr);
					pred.setNext(node2add);
					return true;
				}
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}

	@Override
	public boolean remove(T x) {
		int key = x.hashCode();
		Node<T> curr = this.head;
		Node<T> pred = null;
		while (true) {
			pred = this.head;
			curr = pred.getNext();
			// Traverse list till the correct position
			while (curr.getKey() < key) {
				pred = curr;
				curr = curr.getNext();
			}
			try {
				pred.lock();
				curr.lock();
				if (validate(pred, curr)) {
					// Re-check that still the object to remove
					if (x == curr.getObject()) {
						// Remove
						pred.setNext(curr.getNext());
						return true;
					} else {
						return false;
					}
				}
				return false;
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}

	@Override
	public boolean contains(T x) {
		int key = x.hashCode();
		Node<T> curr = this.head;
		Node<T> pred = null;
		while (true) {
			try {
				pred = this.head;
				curr = pred.getNext();
				// Traverse list till the correct position
				while (curr.getKey() < key) {
					pred = curr;
					curr = curr.getNext();
				}
				pred.lock();
				curr.lock();
				if (validate(pred, curr)) {
					return curr.getKey() == key;
				}
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}

	private boolean validate(Node<T> pred, Node<T> curr) {
		Node<T> node = head;
		while (node.getKey() <= pred.getKey()) {
			if (node == pred)
				return pred.getNext() == curr;
			node = node.getNext();
		}
		return false;
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
