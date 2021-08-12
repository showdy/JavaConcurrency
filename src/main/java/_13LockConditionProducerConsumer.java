import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by showdy on 2021/8/12 14:30
 * <p>
 * Lock-Condition实现生产者消费者模型
 */
public class _13LockConditionProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        Random random = new Random();
        // 生产者-消费者模型缓冲区
        ProducerConsumerQueue<Integer> queue = new ProducerConsumerQueue<>();
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);
        for (int i = 0; i < 3; i++) {
            // 休眠0-50毫秒，增加随机性
            Thread.sleep(random.nextInt(50));
            service.submit(producer);
        }
        for (int i = 0; i < 3; i++) {
            // 休眠0-50毫秒，增加随机性
            Thread.sleep(random.nextInt(50));
            service.submit(consumer);
        }
        // 关闭线程池
        service.shutdown();
    }


    /**
     * 生产者线程
     */
    public static class Producer implements Runnable {

        private final ProducerConsumerQueue<Integer> queue;

        public Producer(ProducerConsumerQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 消费者线程
     */
    public static class Consumer implements Runnable {
        private final ProducerConsumerQueue<Integer> queue;

        public Consumer(ProducerConsumerQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * wait/notify机制实现生产者-消费者模型
     */
    public static class ProducerConsumerQueue<E> {
        /**
         * 队列最大容量
         */
        private final static int QUEUE_MAX_SIZE = 3;
        /**
         * 存放元素的队列
         */
        private final Queue<E> queue;

        private final Lock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();

        public ProducerConsumerQueue() {
            queue = new LinkedList<>();
        }

        /**
         * 向队列中添加元素 * * @param e * @return
         */
        public boolean put(E e) throws InterruptedException {
            lock.lock();
            // 如果队列是已满，则阻塞当前线程
            try {
                while (queue.size() == QUEUE_MAX_SIZE)
                    notFull.await();
                // 队列未满，放入元素，并且通知消费线程
                queue.offer(e);
                System.out.println(Thread.currentThread().getName() + " -> produce element，elements size: " + queue.size());
                notEmpty.signalAll();
                return true;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 从队列中获取元素 * @return
         */
        public E get() throws InterruptedException {
            lock.lock();
            try {
                // 如果队列是空的，则阻塞当前线程
                while (queue.isEmpty())
                    notEmpty.await();
                // 队列非空，取出元素，并通知生产者线程
                E e = queue.poll();
                System.out.println(Thread.currentThread().getName() + " -> consume element，elements size: " + queue.size());
                notFull.signalAll();
                return e;
            } finally {
                lock.unlock();
            }
        }
    }
}
