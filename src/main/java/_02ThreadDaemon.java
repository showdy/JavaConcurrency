import java.util.concurrent.TimeUnit;

/**
 * Created by showdy on 2021/8/6 23:03
 *
 * jvm中分为守护线程和非守护线程，主线程退出后，守护线程会推出；
 */
public class _02ThreadDaemon {

    public static void main(String[] args) {
        DaemonThread thread = new DaemonThread();
        //守护线程在主线程退出后也会退出，java中默认为非守护线程
        thread.setDaemon(true);
        thread.start();
        System.out.println("main thread exit");
    }


    public static class DaemonThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
                try {
                    System.out.println("Daemon thread");
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
