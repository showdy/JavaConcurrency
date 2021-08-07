import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by showdy on 2021/8/6 22:36
 * <p>
 * java多线程中生产者消费模式的实现方式
 * 1. wait 与notify
 * 2. lock 与condition
 */
public class _06ThreadProducerConsumer {

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
                queue.put(i);
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
                queue.get();
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

        public ProducerConsumerQueue() {
            queue = new LinkedList<>();
        }

        /**
         * 向队列中添加元素 * * @param e * @return
         */
        public synchronized boolean put(E e) {
            // 如果队列是已满，则阻塞当前线程
            while (queue.size() == QUEUE_MAX_SIZE) {
                try {
                    wait();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            // 队列未满，放入元素，并且通知消费线程
            queue.offer(e);
            System.out.println(Thread.currentThread().getName() + " -> produce element，elements size: " + queue.size());
            notify();
            return true;
        }

        /**
         * 从队列中获取元素 * @return
         */
        public synchronized E get() {
            // 如果队列是空的，则阻塞当前线程
            while (queue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 队列非空，取出元素，并通知生产者线程
            E e = queue.poll();
            System.out.println(Thread.currentThread().getName() + " -> consume element，elements size: " + queue.size());
            notify();
            return e;
        }
    }
}
