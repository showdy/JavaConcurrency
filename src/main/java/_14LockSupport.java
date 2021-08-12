/**
 * Created by showdy on 2021/8/12 14:58
 *
 * LockSupport.park()和Thread.sleep()的区别从功能上说，LockSupport.park()与Thread.sleep()方法类似，都是让线程阻塞，二者的区别如下：
 * （1）Thread.sleep()没法从外部唤醒，只能自己醒过来；而被LockSupport.park()方法阻塞的线程可以通过调用LockSupport.unpark()方法去唤醒。
 * （2）Thread.sleep()方法声明了InterruptedException中断异常，这是一个受检异常，调用者需要捕获这个异常或者再抛出；而调用LockSupport.park()方法时不需要捕获中断异常。
 * （3）被LockSupport.park()方法、Thread.sleep()方法所阻塞的线程有一个特点，当被阻塞线程的Thread.interrupt()方法被调用时，
 *   被阻塞线程的中断标志将被设置，该线程将被唤醒。不同的是，二者对中断信号的响应方式不同：LockSupport.park()方法不会抛出InterruptedException异常，
 *   仅仅设置了线程的中断标志；而Thread.sleep()方法会抛出InterruptedException异常。
 * （4）与Thread.sleep()相比，调用LockSupport.park()能更精准、更加灵活地阻塞、唤醒指定线程。
 * （5）Thread.sleep()本身就是一个Native方法；LockSupport.park()并不是一个Native方法，只是调用了一个Unsafe类的Native方法（名字也叫park）去实现。
 * （6）LockSupport.park()方法还允许设置一个Blocker对象，主要用来供监视工具或诊断工具确定线程受阻塞的原因。
 *
 * LockSupport.park()与Object.wait()的区别从功能上说，LockSupport.park()与Object.wait()方法也类似，都是让线程阻塞，二者的区别如下：
 * （1）Object.wait()方法需要在synchronized块中执行，而LockSupport.park()可以在任意地方执行。
 * （2）当被阻塞线程被中断时，Object.wait()方法抛出了中断异常，调用者需要捕获或者再抛出；当被阻塞线程被中断时，
 * LockSupport.park()不会抛出异常，调用时不需要处理中断异常。
 * （3）如果线程在没有被Object.wait()阻塞之前被Object.notify()唤醒，也就是说在Object.wait()执行之前去执行Object.notify()，
 * 就会抛出IllegalMonitorStateException异常，是不被允许的；而线程在没有被LockSupport.park()阻塞之前被LockSupport.unPark()唤醒，
 * 也就是说在LockSupport.park()执行之前去执行LockSupport.unPark()，不会抛出任何异常，是被允许的。
 */
public class _14LockSupport {

    public static void main(String[] args) {

    }
}
