import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by showdy on 2021/8/6 22:40
 *  线程 中断的方式：标志位
 */
public class _03ThreadFlagInterrupt {
    public static void main(String[] args) throws InterruptedException {
        ThreadFlag t = new ThreadFlag();
        t.start();
        Thread.sleep(6);
        t.cancel();
        //等待子线程执行完成
        t.join();
    }


    //1. 传统标志位方式中断线程，如何线程任务中有wait之类的阻塞方式，线程可能不会被中断
    public static class ThreadFlag extends Thread{

        private boolean stopped = false;

        private final Object lock = new Object();

        @Override
        public void run() {
            while (!stopped){
                try {
                    System.out.println("thread running...");
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel(){
            stopped = true;
        }
    }

    //线程中使用阻塞方法，导致线程无法使用标记被中断
    static class BrokenPrimeProducer extends Thread {
        private final BlockingQueue<BigInteger> queue;
        private volatile boolean cancelled = false;

        BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!cancelled)
                    //此处阻塞,可能永远无法检测到结束的标记
                    queue.put(p = p.nextProbablePrime());
            } catch (InterruptedException consumed) {
            }
        }

        public void cancel() {
            cancelled = true;
        }
    }
}
