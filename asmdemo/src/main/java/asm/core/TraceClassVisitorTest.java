package asm.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * TraceClassVisitor 顾名思义，
 * 我们可以“trace”也就是打印一些信息，这些信息就是ClassWriter 提供给我们的byte字节数组。
 * 因为我们阅读一个二进制字节流还是比较难以理解和解析一个类文件的结构。
 * TraceClassVisitor通过初始化一个classWriter 和一个Printer对象，来实现打印我们需要的字节流信息。
 * 通过TraceClassVisitor 我们能更好地比较两个类文件，更轻松得分析class的数据结构。
 *
 *   下面看个例子，我们用TraceClassVisitor 来打印Task 类信息。
 */
public class TraceClassVisitorTest {
    public static void main(String[] args) throws IOException {
        ClassReader cr = new ClassReader("asm.core.Task");
        ClassWriter cw = new ClassWriter(0);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cr.accept(cv, 0);

    }
}
