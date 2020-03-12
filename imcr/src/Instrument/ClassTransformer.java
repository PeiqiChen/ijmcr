package Instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.FieldVisitor;

/**
 * 不加adapter 就是 reader 咋进来 writer 咋出去
 * adpter 在 read和 write之间进行修改
 * ClassTransformer 这就是一个 adapter
 *
 *
 * visit()只是简单访问整个类
 * visitMethod 和 visitField 详细访问
 */
public class ClassTransformer extends ClassVisitor {
    //在method中拿到类名
    private String className;



    ClassTransformer(ClassVisitor cv){
        //asm5 -version
        //opcodes保存字节码指令对应的数字
        super(Opcodes.ASM5,cv);
    }


    /**
     * 访问一些方法
     * @param version
     * @param access
     * @param name 类名
     * @param signature
     * @param superName 只有object没有supername
     * @param interfaces
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        System.out.println("name"+name);
        className=name;//name就是类名 在method中拿到类名
        cv.visit(version, access, name, signature, superName, interfaces);
    }


    /**
     * 访问field 和method
     * 一个类有多个方法 因此通过以下一下方法 会return很多MethodVisitor
     * 但一个类只用生成一个ClassTransformer实例
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv= cv.visitMethod(access,name,desc,signature,exceptions);
        mv = new MethodTransformer(mv, access, className, name, desc);//classname属性要从 MethodTransformer 传过来
        return mv;
    }
    @Override

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return cv.visitField(access, name, desc, signature, value);
    }

    /**
     * 访问内部类 即person
     */
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }
}
