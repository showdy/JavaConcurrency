import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by showdy on 2021/8/9 11:13
 * CAS:轻量级别的无锁自旋方式
 * JDK的rt.jar包中的Unsafe类提供了硬件级别的原子性操作，Unsafe类中的方法都是native方法，它们使用JNI的方式访问本地C++ 实现库
 *
 * CAS 操作的弊端：
 * 1. ABA问题
 * 2. 只能保证一个共享变量的原子操作，如需操作多个，将多个变量合成一个对象，使用AtomicReference操作
 * 3. 开销大,解决方式一般是使用，空间换时间
 *   （1）分散操作热点，使用LongAdder替代基础原子类AtomicLong，LongAdder将单个CAS热点（value值）分散到一个cells数组中。
 *   （2）使用队列削峰，将发生CAS争用的线程加入一个队列中排队，降低CAS争用的激烈程度。JUC中非常重要的基础类AQS（抽象队列同步器）就是这么做的。
 */
public class _09Unsafe {

    public static void main(String[] args) throws InterruptedException {
        final OptimisticLockPlus cas = new OptimisticLockPlus();
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.execute(()->{
                for (int j = 0; j < 1000; j++) {
                    cas.setPlus();
                }
                latch.countDown();
            });
        }
        latch.await(); //主线程等待latch倒数完毕
        executor.shutdown();
        System.out.println("累计和："+cas.value);
        System.out.println("失败次数："+OptimisticLockPlus.failCount.get());
    }


    static class OptimisticLockPlus {
        private static final int THREAD_COUNT = 10;
        //内部值，保证可见性
        private volatile int value;
        //直接获取：java.lang.SecurityException: Unsafe
        //private static final Unsafe unsafe = Unsafe.getUnsafe();
        private static Unsafe unsafe =null;
        //value的内存偏移
        private static long valueOffset = 0;
        //统计失败次数
        private static final AtomicLong failCount = new AtomicLong(0);

        static {
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe) field.get(null);
                valueOffset = unsafe.objectFieldOffset(OptimisticLockPlus.class.getDeclaredField("value"));
                System.out.println("valueOffset:" + valueOffset);
            } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public final boolean unSafeCompareAndSet(int oldValue, int newValue) {
            //通过CAS操作，比较并交换
            return unsafe.compareAndSwapInt(this, valueOffset, oldValue, newValue);
        }

        //无锁编程实现安全自增
        public void setPlus() {
            int oldValue = value;
            int i = 0;
            do {
                oldValue = value;
                if (i++ > 1) {
                    failCount.incrementAndGet();
                }
            } while (!unSafeCompareAndSet(oldValue, oldValue + 1));
        }

    }
}
