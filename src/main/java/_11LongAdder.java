/**
 * Created by showdy on 2021/8/9 15:18
 *  Java 8提供了一个新的类LongAdder，以空间换时间的方式提升高并发场景下CAS操作的性能。
 *  LongAdder的核心思想是热点分离，与ConcurrentHashMap的设计思想类似：将value值分离成一个数组，
 *  当多线程访问时，通过Hash算法将线程映射到数组的一个元素进行操作；而获取最终的value结果时，则将数组的元素求和。
 *
 *  低并发时，AtomicLong性能比LongAdder性能好，高并发时，LongAdder比AtomicLong性能好。
 */
public class _11LongAdder {
}
