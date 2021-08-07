/**
 * Created by showdy on 2021/8/7 14:23
 * <p>
 * volatile关键字：64位写入的原子性、内存可见性和禁止重排序
 */
public class _07Volatile {


    //DCL
    public static class Singleton {
        private static volatile Singleton sInstance;

        private Singleton() {
        }

        /**
         * instance=new Instance（）代码有问题：其底层会分为三个操作：
         * （1）分配一块内存。（2）在内存上初始化成员变量。（3）把instance引用指向内存。
         * 在这三个操作中，操作（2）和操作（3）可能重排序，即先把instance指向内存，再初始化成员变量，因为二者并没有先后的依赖关系。
         * 此时，另外一个线程可能拿到一个未完全初始化的对象。这时，直接访问里面的成员变量，就可能出错。这就是典型的“构造函数溢出”问题。
         * 解决办法也很简单，就是为instance变量加上volatile修饰。
         */
        public static Singleton getInstance() {
            if (sInstance == null) {
                synchronized (Singleton.class) {
                    if (sInstance == null) {
                        sInstance = new Singleton();
                    }
                }
            }
            return sInstance;
        }
    }
}
