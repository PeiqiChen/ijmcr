package asm.core;
import org.objectweb.asm.ClassReader;
import java.io.IOException;

public class ClassesPrintTest {
    public static void main(String[] args) {
        try{
            ClassReader cr = new ClassReader("asm.core.Task");
            ClassPrintVisitor cp = new ClassPrintVisitor();
            /**
             * 将一个ClassPrintVisitor 对象传给ClassReader。
             * ClassReader作为一个解析事件的producer 并且由ClassPrintVisitor去消费（处理打印逻辑）。
             * accept()方法就将Task 字节码进行解析，然后调用ClassPrintVisitor 的方法。 
             */
            cr.accept(cp,0);

            /**
             * 通过这个byte数组我们可以通过ClassLoader来加载我们的类，
             * 也可以用FileOutputStream来生成个class文件。
             */
            cp.gen();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
