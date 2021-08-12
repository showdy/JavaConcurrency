import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by showdy on 2021/8/12 14:06
 * <p>
 * ReentrantLock：可重入自旋锁，独占模式，底层有AQS实现
 */
public class _12ReentrantLock {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ShareData data = new ShareData();
        Lock lock = new ReentrantLock();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    data.increaseNum(lock);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executor.shutdown();
        long totalTime = System.currentTimeMillis() - start;
        System.out.println("consumed time:" + totalTime);
        System.out.println("shared num value: " + data.num);
    }


    public static class ShareData {

        public volatile int num = 0;

        public void increaseNum(Lock lock) {
            //Lock可保证原子操作，但是不能保证可见性
            lock.lock();
            try {
                num++;
            } finally {
                lock.unlock();
            }
        }
    }
}
