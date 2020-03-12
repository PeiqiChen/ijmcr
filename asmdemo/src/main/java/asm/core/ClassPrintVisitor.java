package asm.core;
import org.objectweb.asm.*;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;

public class ClassPrintVisitor extends ClassVisitor{
    ClassPrintVisitor(){
        super(Opcodes.ASM4);
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces){
        System.out.println(name + " extends " + superName + " {");
    }


    /**
     * 这里我们打印了is开头的属性名以及描述，方法名以及描述。
     * 这里的desc其实是class文件属性修饰或者方法参数、返回值的全限定名（fully qualified name）。
     * 这里isTask后面的I 代表的是int类型的描述。IsTask()方法后面的V表示返回值是void。
     */
    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value){
        if (name.startsWith("is")) {
            System.out.println(" field name: " + name + desc);
        }
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions){
        if(name.startsWith("is")){
            System.out.println(" start with is method: " + name + desc);
        }
        return null;
    }

    @Override
    public void visitEnd(){
        System.out.println("}");
    }


    /**
     * 生成一个能代表class文件的字节数组
     * 通过这个byte数组我们可以通过ClassLoader来加载我们的类，也可以用FileOutputStream来生成个class文件。
     *
     * 但是field名字和method是写死的 不灵活
     * @return
     */
    public static byte[] gen(){
        String className="asm/core/ChildClass";
        String superName="asm/core/ParentInter";

        ClassWriter cw = new ClassWriter(0);// ClassWriter 继承自ClassVisitor

        // 我们需要先调用visit 方法生成一个class的头部信息
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT ,
                className, null, "java/lang/Object", new String[]{superName}
        );


        //visitField()和visitMethod()方法分别生成我们的属性和方法
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "zero", "I", null, new Integer(0))
                .visitEnd();
        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null)
                .visitEnd();

        // 调用cw.visitEnd()结束整个创建过程
        cw.visitEnd();

        try{
            File dir = new File("out");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File classFile = new File(dir, className.replace("/", ".") + ".class");
            System.out.println("class file path: " + classFile.getAbsolutePath());
            classFile.createNewFile();
            System.out.println("Writing " + className);
            byte[] code = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(code);
            fos.close();
        }catch (Exception e){ e.printStackTrace();}

        return cw.toByteArray();
    }
}
