import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class test {
	public static void main(String[] args) {
		Map<Integer, Integer> a = new ConcurrentHashMap<Integer, Integer>();
		ReentrantLockTest.fair();
	}

	public static class ReentrantLockTest {

		static Lock fairLock = new ReentrantLock(true);
		private static Lock unfairLock = new ReentrantLock();

		class IntComparator implements Comparator<Integer> {
			@Override
			public int compare(Integer x, Integer y) {
				// Assume neither string is null. Real code should
				// probably be more robust
				// You could also just return x.length() - y.length(),
				// which would be more efficient.
				if (x < y) {
					return -1;
				}
				if (x > y) {
					return 1;
				}
				return 0;
			}
		}
		
		static class PriorityRequest implements Comparable<PriorityRequest> {
			private String timestamp;
			private String key;
			private String request;

			public PriorityRequest(String iTimestamp, String iKey, String iRequest) {
				timestamp = iTimestamp;
				key = iKey;
				request = iRequest;
			}

			public String getTimeStamp() {
				return timestamp;
			}
			
			public String getKey() {
				return key;
			}

			public String getRequest() {
				return request;
			}
			
			@Override
			public int compareTo(PriorityRequest p) {
				int indicator = timestamp.compareTo(p.timestamp);
				return indicator < 0 ? -1 : indicator > 0 ? 1 : 0;
			}

			public boolean fastEqual(String iTimestamp, String iKey, String iRequest) {
				return timestamp.equals(iTimestamp) && key.equals(iKey) && request.equals(iRequest);
			}
		}

		public static void fair() {
			System.out.println("fair version");
			Queue<PriorityRequest> q = new PriorityBlockingQueue<PriorityRequest>(
//					new Comparator<PriorityRequest>() {
//				@Override
//				public int compare(PriorityRequest a, PriorityRequest b) {
//					int indicator = a.getTimeStamp().compareTo(b.getTimeStamp());
////					System.out.println(a.getTimeStamp() + "," + b.getTimeStamp() + "," +indicator);
//					return indicator < 0 ? -1 : indicator > 0 ? 1 : 0;
//				}
//			}
			);
			for (int i = 0; i < 10; i++) {
				// fairLock.lock();
				Thread thread = new Thread(new Job(q, i, fairLock));
				thread.setName("" + i);
				thread.start();
				// try {
				// Thread.sleep(2);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// fairLock.unlock();

			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (!q.isEmpty()) {
				// System.out.println("Size:"+q.size());
				System.out.println(q.poll().getTimeStamp());
			}
		}

		private static class Job implements Runnable {
			private Queue<PriorityRequest> q;
			private int p;
			private Lock l;

			public Job(Queue q, int p, Lock l) {
				this.q = q;
				this.p = p;
				this.l = l;
			}

			@Override
			public void run() {
				l.lock();
				Random rand = new Random();
				PriorityRequest tmp = new PriorityRequest(rand.nextInt(100)+"", "hi", "Get");
//				q.add(tmp);
				l.unlock();
			}
		}
	}

}
