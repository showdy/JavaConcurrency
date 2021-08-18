package obj;

/**
 * Created by showdy on 2021/8/18 9:56
 * 浅拷贝（shadow copy）： 把原型对象中成员变量为值类型的属性都复制给克隆对象，把原型对象中成员变量为引用类型的引用地址也复制给克隆对象（引用传递），
 * 深拷贝(Deep Clone): 是将原型对象中的所有类型，无论是值类型还是引用类型，都复制一份给克隆对象(值传递），即堆内存中存在不同的对象，而不是同一对象的地址的引用。
 *      方式：
 *      1. 序列化（serialization）这个对象，再反序列化回来，就可以得到这个新的对象，无非就是序列化的规则需要我们自己来写。
 *      2. 利用 clone() 方法，既然 clone() 方法，是我们来重写的，实际上我们可以对其内的引用类型的变量，再进行一次 clone()。
 */
public class _01Clone {

    public static void main(String[] args) {
        A objectA = new A("hello", 20, new B("PUA"));
        A cloneA = (A) objectA.clone();
        System.out.println(objectA == cloneA);
        System.out.println(objectA.hashCode());
        System.out.println(cloneA.hashCode());
        System.out.println(objectA.propertyB.hashCode());
        System.out.println(cloneA.propertyB.hashCode());
    }


    //使用cloneable方实现深拷贝
    public static class A implements Cloneable {
        private String name;
        private int age;
        private B propertyB;

        public A() {
        }

        public A(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public A(String name, int age, B propertyB) {
            this.name = name;
            this.age = age;
            this.propertyB = propertyB;
        }

        @Override
        protected Object clone() {
            try {
                //浅拷贝
                A cloneA = (A) super.clone();
                cloneA.propertyB = (B) this.propertyB.clone();
                return cloneA;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class B implements Cloneable {
        private String hobby;

        public B(String hobby) {
            this.hobby = hobby;
        }

        @Override
        protected Object clone() {
            try {
                //浅拷贝
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
