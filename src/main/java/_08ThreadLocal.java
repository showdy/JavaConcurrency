import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by showdy on 2021/8/7 14:43
 * ThreadLocal相关核心：
 * 1. 内部数据结构
 * 2. hash冲突如何解决 key.hashcode & (len-1)
 * 3. 强弱引用，及如何解决ThreadLocal内存泄漏问题
 *
 * @see https://www.jianshu.com/p/acfd2239c9f4
 * @see https://gitbook.cn/books/5eb88fc961d1bb45a6c87e13/index.html
 */
public class _08ThreadLocal {

    public static void main(String[] args)  {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CyclicBarrier barrier = new CyclicBarrier(10,()->{
            service.shutdown();
            System.out.println("all thread executed completed");
        });
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "=> thread get Id:" + ThreadId.get());
                ThreadId.setId();
                System.out.println(Thread.currentThread().getName() + "=> thread set and get Id:" + ThreadId.get());
                ThreadId.remove();
                System.out.println(Thread.currentThread().getName() + "=> thread remove Id:" + ThreadId.get());
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }


    }


    public static class ThreadId {
        // Atomic integer containing the next thread ID to be assigned
        private static final AtomicInteger nextId = new AtomicInteger(0);

        // Thread local variable containing each thread's ID
        private static final ThreadLocal<Integer> threadId =
                new ThreadLocal<Integer>() {
                    //没有set 而使用get获取值时会初始化一个默认值。
                    @Override
                    protected Integer initialValue() {
                        return nextId.getAndIncrement();
                    }
                };

        // Returns the current thread's unique ID, assigning it if necessary
        public static int get() {
            return threadId.get();
        }

        public static void setId() {
            threadId.set(threadId.get() + 10);
        }

        public static void remove() {
            threadId.remove();
        }
    }
}
