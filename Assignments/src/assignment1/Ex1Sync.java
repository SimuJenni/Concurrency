package assignment1;

public class Ex1Sync {

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 3) {
			// Parse the inputs
			int n = Integer.parseInt(args[0]);
			int m = Integer.parseInt(args[1]);
			int numItr = Integer.parseInt(args[2]);
			
			Ex1Runner runner = new Ex1Runner(new SharedCounterSync());
			runner.startAndRun(n, m, numItr);

		} else {
			System.out.println("Expected three integers as input!");
		}
	}
}

class SharedCounterSync implements Counter {
	int counter = 0;

	public synchronized void decrement() {
		counter--;
	}

	public synchronized void increment() {
		counter++;
	}

	public long getCounter() {
		return counter;
	}
}
