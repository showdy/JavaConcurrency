import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by showdy on 2021/8/6 22:34
 * <p>
 * 创建线程的方式
 */
public class _01ThreadCreate {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 方式1：thread
        new MyThread().start();
        //方式2：runnable
        new Thread(()->{
            System.out.println("Created Thread by Runnable");
        }).start();
        //方式3：Callable
        FutureTask<String> task = new FutureTask<String>(()->{
            System.out.println("Created Thread by Callable");
            return UUID.randomUUID().toString();
        });
        new Thread(task).start();
        //阻塞方法，会阻塞主线程
        System.out.println(task.get());
    }

    public static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Created thread by extend Thread");
        }
    }
}
