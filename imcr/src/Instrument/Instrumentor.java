package Instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;

import java.lang.instrument.Instrumentation;
import java.io.IOException;
import java.security.ProtectionDomain;

public class Instrumentor {

    public static boolean shouldInstrumentClass(String className){
        if(className.contains("test/")){
            return true;
        }
        return false;
    }


    /**
     * 在 visit() 是拿到一些基本信息
     * 加 agent 改 .class 文件
     * @param agentArgs
     * @param inst
     */
    public static void premain (String agentArgs, Instrumentation inst){
        inst.addTransformer(new ClassFileTransformer() {
            /**
             *
             * @param loader
             * @param className
             * @param classBeingRedefined 加载到jvm之前拿不到class文件 已经加载到jvm
             * @param protectionDomain
             * @param classfileBuffer 拦截到的字节码文件 javap反编译出来的东西
             * @return
             * @throws IllegalClassFormatException
             */
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                /**
                 * mcr在加载进来之前 判断了一下该类是否需要被插桩
                 * 比如包名有test 就插桩
                 */
                if(shouldInstrumentClass(className)){
//                    System.out.println("className"+className);

                    /**
                     * 第二个参数 局部变量自动计算 省略了自己计算局部变量表c
                     * cr ct cw执行顺序
                     * cr cw 可以插入任意多的adapter
                     *
                     * 不加adapter 就是reader 咋进来 writer咋出去
                     * adpter 在 reader 和writer 之间进行修改
                     */
                    ClassReader cr=new ClassReader(classfileBuffer);
                    ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);

                    /**
                     * 这是一个 adapter
                     * 一个类有多个方法 因此通过以下一下方法 会return很多MethodVisitor
                     * 但一个类只用生成一个ClassTransformer实例
                     */
                    ClassTransformer classTransformer =new ClassTransformer(cw);


                    //绑定classtranformer 即绑定一个adapter
//                    System.out.println(classTransformer);
                    cr.accept(classTransformer,ClassReader.EXPAND_FRAMES);


                    classfileBuffer=cw.toByteArray();
//                    System.out.println(className);

                    //--------------------------------如果要插桩就保留下来--------------------------------------
                    File dir = new File("genClasses");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    File file=new File(dir,className.replace("/", ".") + ".class");
                    FileOutputStream fos;

                    try{
                        fos = new FileOutputStream(file);
                        fos.write(classfileBuffer);
                        fos.close();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    //-------------------------------------------------------------------------------------------

                }
                return classfileBuffer;
            }
        });
    }
}
