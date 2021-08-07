/**
 * Created by showdy on 2021/8/6 22:35
 * <p>
 * 演示如何使用synchronized关键字
 * <p>
 * 锁对象包含：
 * 1. 对象内部有个标志位（state 为0 或者1），记录对象是否被某个线程占用
 * 2. 对象内部得有记录thread ID，表示被哪个线程占用
 * 3. 对象内部得维护一个thread ID list，记录哪些线程在等待对象
 * 既然资源是共享对象，锁也共享对象，可以合二为一。
 * <p>
 * Synchronized原理
 * 在对象头里，有一块数据叫Mark Word。在64位机器上，Mark Word是8字节（64位）的，这64位中有2个重要字段：锁标志位和占用该锁的thread ID
 * <p>
 * synchronize修饰成员方法时，锁对象为this，即调用该方法的对象
 * synchronized修饰静态方式时，锁对象为class对象
 *
 * 一个线程可以从挂起状态变为可以运行状态（也就是被唤醒），即使该线程没有被其他线程调用notify（）、notifyAll（）方法进行通知，
 * 或者被中断，或者等待超时，这就是所谓的虚假唤醒。
 * 做法就是不停地去测试该线程被唤醒的条件是否满足，不满足则继续等待，也就是说在一个循环中调用wait（）方法进行防范.
 *
 * 生产者本来只想通知消费者，但它把其他的生产者也通知了；消费者本来只想通知生产者，但它被其他的消费者通知了。
 * 原因就是wait（）和notify（）所作用的对象和synchronized所作用的对象是同一个，只能有一个对象，无法区分队列空和列队满两个条件。
 * 这正是Condition要解决的问题。
 */
public class _05ThreadSynchronized {

    public static void main(String[] args) {
        SharedData sharedData = new SharedData();
        for (int i = 0; i < 10; i++) {
            int tempI = i;
            new Thread(() -> {
                if (tempI % 2 == 0) {
                    sharedData.increaseNum();
                } else {
                    sharedData.decreaseNum();
                }
            }).start();
        }
    }


    static class SharedData {

        private int num = 0;
        private final Object lock = new Object();

        //静态方法锁对象为 SharedData.class
        public static synchronized void printNum() {
            System.out.println("try to print num");
        }

        public synchronized void printRealNum() {
            System.out.println("try to print real num: " + num);
        }

        public void decreaseNum() {
            synchronized (lock) {
                while (num <= 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                num--;
                System.out.println("num decrease now is: "+num);
                lock.notifyAll();
            }
        }

        public void increaseNum() {
            synchronized (lock) {
                //防止虚假唤醒，需要循环检查
                while (num >= 10) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                num++;
                System.out.println("num increase now is: "+num);
                lock.notifyAll();
            }
        }
    }
}
