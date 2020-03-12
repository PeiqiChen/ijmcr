package com.chen.asm;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.IOException;
import java.io.PrintWriter;

public class TypeTest {
    /**
     * TraceClassVisitor用来查看生成的字节码对应的类是否是想要的类。
     *
     * @param args
     * @throws IOException
     */
//    public static void main(String[] args) throws IOException {
//        ClassWriter cw=new ClassWriter(3);
//        TraceClassVisitor tv=new TraceClassVisitor(cw,new PrintWriter(System.out));
//
//        tv.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC+Opcodes.ACC_ABSTRACT+Opcodes.ACC_INTERFACE,
//                "org/by/Cwtest",null,"java/lang/Object",
//                null);
//        tv.visitField(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"LESS","I",
//                null,new Integer(-1)).visitEnd();
//        tv.visitField( Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"EQUAL","I",
//                null,new Integer(0)).visitEnd();
//        tv.visitField(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"GRATER","I",
//                null,new Integer(1)).visitEnd();
//
//        tv.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_ABSTRACT,"compareTo","(Ljava/lang/Object;)I",
//                null,null).visitEnd();
//
//        tv.visitEnd();
//    }

    /**
     * CheckClassAdapter这类是用来检查它的方法调用以及参数是否正确。
     */
    public static void main(String[] args)throws IOException {
        ClassWriter cw=new ClassWriter(3);
        CheckClassAdapter cca= new CheckClassAdapter(cw);
        TraceClassVisitor tv=new TraceClassVisitor(cca, new PrintWriter(System.out));

        tv.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC+Opcodes.ACC_ABSTRACT+Opcodes.ACC_INTERFACE,
                "org/by/Cwtest",null,"java/lang/Object",
                null);
        tv.visitField(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"LESS","I",
                null,new Integer(-1)).visitEnd();
        tv.visitField(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"EQUAL","I",
                null,new Integer(0)).visitEnd();
        tv.visitField(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC+Opcodes.ACC_FINAL,"GRATER","I",
                null,new Integer(1)).visitEnd();

        tv.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_ABSTRACT,"compareTo","(Ljava/lang/Object;)I",
                null,null).visitEnd();

        tv.visitEnd();
    }
}
