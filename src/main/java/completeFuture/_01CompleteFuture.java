package completeFuture;

import org.omg.CORBA.TIMEOUT;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by showdy on 2021/8/16 21:20
 *
 * CompleteFuture 是java8 对Future对增强，丰富异步编程。
 */
public class _01CompleteFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        thenCompose();
        thenCombine();
        anyOf();
    }

    public static void thenRunMethod(){
        Supplier<String> supplier = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello_world";
        };
        //thenRun 无参数也无返回结果
        CompletableFuture<Void> thenRunFuture = CompletableFuture.supplyAsync(supplier).thenRun(() -> {
            System.out.println("run thenRun");
        });
        //thenAccept 由参数无返回结果
        CompletableFuture<Void> thenAcceptFuture = CompletableFuture.supplyAsync(supplier).thenAccept((result) -> {
            System.out.println("thenAccept:" + result);
        });
        //thenApply 有参数也有返回结果
        CompletableFuture<String> thenApplyFuture =
                CompletableFuture.supplyAsync(supplier).thenApply(result-> result.toUpperCase(Locale.CHINA));
        try {
            System.out.println(thenRunFuture.get());
            System.out.println(thenAcceptFuture.get());
            System.out.println(thenApplyFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void thenCompose(){
        CompletableFuture<String> helloFuture = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> worldFuture = CompletableFuture.supplyAsync(() -> "world");
        //thenCompose: 类似于flatmap操作符号
        helloFuture.thenCompose(result->worldFuture).thenAccept(System.out::println);
    }

    public static void thenCombine(){
        CompletableFuture<String> helloFuture = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> worldFuture = CompletableFuture.supplyAsync(() -> "world");
        //thenCompose：类似于combine操作符
        helloFuture.thenCombine(worldFuture, String::concat).thenAccept(System.out::println);
    }

    public static void anyOf(){
        CompletableFuture<String> helloFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        });
        CompletableFuture<String> worldFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        });
        CompletableFuture<String> javaFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "java";
        });

        try {
            Object result = CompletableFuture.anyOf(helloFuture, worldFuture, javaFuture).get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
