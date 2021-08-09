import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.function.UnaryOperator;

/**
 * Created by showdy on 2021/8/9 11:11
 * <p>
 * 基本原子类：
 * · AtomicInteger：整型原子类。
 * · AtomicLong：长整型原子类。
 * · AtomicBoolean：布尔型原子类。
 * 数组原子类：
 * · AtomicIntegerArray：整型数组原子类。
 * · AtomicLongArray：长整型数组原子类。
 * · AtomicReferenceArray：引用类型数组原子类。
 * 引用原子类：
 * · AtomicReference：引用类型原子类。
 * · AtomicMarkableReference：带有更新标记位的原子引用类型。
 *      --AtomicMarkableReference适用于只要知道对象是否被修改过，而不适用于对象被反复修改的场景。
 * · AtomicStampedReference：带有更新版本号的原子引用类型--解决ABA问题。
 *      --AtomicStampReference的compareAndSet()方法首先检查当前的对象引用值是否等于预期引用，并且当前印戳（Stamp）标志是否等于预期标志，
 *      如果全部相等，就以原子方式将引用值和印戳（Stamp）标志的值更新为给定的更新值。
 */
public class _10AtomicClass {

    public static void main(String[] args) {
//        testAtomicInteger();
//        testAtomicReference();
//        testAtomicStampReference();
        testAtomicMarkableReference();
    }


    public static void testAtomicInteger() {
        AtomicInteger ai = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                for (int j = 0; j < 1000; j++) {
                    ai.getAndIncrement();
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("total integer:" + ai.get());
    }

    public static void testAtomicReference() {
        AtomicReference<User> reference = new AtomicReference<>();
        User oldUser = new User(10, "sh");
        reference.set(oldUser);
        System.out.println(reference.get().name);
        User newUser = new User(20, "down");
        reference.compareAndSet(oldUser, newUser);
        System.out.println(reference.get().name);

        AtomicReferenceFieldUpdater<User, String> updater
                = AtomicReferenceFieldUpdater.newUpdater(User.class, String.class, "name");
        User user = new User(1, "showdy");
        updater.compareAndSet(user, "showdy", "hello");
        System.out.println(updater.updateAndGet(user, String::toUpperCase));
    }

    public static void testAtomicStampReference() {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicStampedReference<Integer> ref = new AtomicStampedReference<>(1, 0);
        new Thread(() -> {
            try {
                boolean success = false;
                int stamp = ref.getStamp();
                System.out.println("before sleep: value = " + ref.getReference() + " stamp=" + ref.getStamp());
                TimeUnit.SECONDS.sleep(5);
                success = ref.compareAndSet(1, 10, stamp, ++stamp);
                System.out.println("after sleep cas 1: success = " + success + " value= " + ref.getReference() + " stamp=" + ref.getStamp());
                success = ref.compareAndSet(10, 1, stamp, ++stamp);
                System.out.println("after sleep cas 2: success = " + success + " value= " + ref.getReference() + " stamp=" + ref.getStamp());
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                boolean success = false;
                int stamp = ref.getStamp();
                System.out.println("before sleep: value = " + ref.getReference() + " stamp=" + ref.getStamp());
                TimeUnit.SECONDS.sleep(10);
                //stamp 此时已经改掉了，所以自旋修改会失败，典型的ABA问题。
                System.out.println("after sleep: stamp=" + ref.getStamp());
                success = ref.compareAndSet(1, 10, stamp, ++stamp);
                System.out.println("after sleep cas 3: success = " + success + " value= " + ref.getReference() + " stamp=" + ref.getStamp());
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main exit");

    }

    public static void testAtomicMarkableReference() {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicMarkableReference<Integer> ref = new AtomicMarkableReference<>(1, false);
        new Thread(() -> {
            try {
                boolean success = false;
                boolean markable = getMark(ref);
                System.out.println("before sleep: value = " + ref.getReference() + " markable=" + markable);
                TimeUnit.SECONDS.sleep(5);
                success = ref.compareAndSet(1, 10, markable, !markable);
                System.out.println("after sleep cas 1: success = " + success + " value= " + ref.getReference() + " markable=" + getMark(ref));
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                boolean success = false;
                boolean markable = getMark(ref);
                System.out.println("before sleep: value = " + ref.getReference() + " markable=" + getMark(ref));
                TimeUnit.SECONDS.sleep(8);
                System.out.println("after sleep:  markable=" +  getMark(ref));
                success = ref.compareAndSet(1, 20, markable, !markable);
                System.out.println("after sleep cas 2: success = " + success + " value= " + ref.getReference() + " markable=" + getMark(ref));
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main exit");

    }

    private static Boolean getMark(AtomicMarkableReference<Integer> ref) {
        boolean[] markHolder = {false};
        Integer value = ref.get(markHolder);
        return markHolder[0];
    }

    static class User implements Serializable {
        int age;
        //必须要volatile参数
        volatile String name;

        public User(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
