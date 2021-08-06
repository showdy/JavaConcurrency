import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by showdy on 2021/8/6 23:23
 *
 *
 * Java中有三种方式可以终止当前运行的线程:
 *
 * 1. 设置某个＂已请求取消（Cancellation Requested)＂标记,而任务将定期查看该标记的协作机制来中断线程.
 * 2. 使用Thread.stop()强制终止线程,但是因为这个方法"解锁"导致共享数据结构处于不一致而不安全被废弃.
 * 3. 使用Interruption中断机制.
 *
 * Thread类中的中断方法:
 *  public void interrupt()
 *  请求中断,设置中断标记,而并不是真正中断一个正在运行的线程,只是发出了一个请求中断的请求,由线程在合适的时候中断自己.
 *
 *  public static native boolean interrupted();
 *  判断线程是否中断,会擦除中断标记(判断的是当前运行的线程),另外若调用Thread.interrupted()返回为true时,必须要处理,
 *  可以抛出中断异常或者再次调用interrupt()来恢复中断.
 *
 *  public native boolean isInterrupted();
 *  判断线程是否中断,不会擦除中断标记
 *
 *  {@link Thread#interrupt}作用其实不是中断线程,而请求线程中断.具体来说,当调用interrupt()方法时:
 *  1. 如果线程处于阻塞状态时(例如处于sleep,wait,join等状态时)那么线程将立即退出阻塞状态而抛出InterruptedException异常.
 *  2. 如果 线程处于正常活动状态,那么会将线程的中断标记设置为true,仅此而已.被设置中断标记的线程将继续运行而不受影响.
 *
 * interrupt()并不能真正的中断线程,需要被调用的线程自己进行配合才行:
 *  1. 在正常运行任务时，经常检查本线程的中断标志位，如果被设置了中断标志就自行停止线程。
 *  2. 在调用阻塞方法时正确处理InterruptedException异常。
 */
public class _04Thread_Interrupted {

    public static void main(String[] args) throws InterruptedException {
        PrimeProducer producer = new PrimeProducer(new LinkedBlockingDeque<>());
        producer.start();
        Thread.sleep(10);
        producer.cancel();
        System.out.println("main exit");
    }


    static class PrimeProducer extends Thread {
        private final BlockingQueue<BigInteger> queue;

        PrimeProducer(BlockingQueue<BigInteger> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!Thread.currentThread().isInterrupted()){
                    p = p.nextProbablePrime();
                    System.out.println("put queue => "+p);
                    Thread.sleep(2);
                    queue.put(p);
                }
            } catch (InterruptedException consumed) {
                /* Allow thread to exit */
            }
        }

        public void cancel() {
            interrupt();
        }
    }
}
