package Instrument;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;
import jdk.internal.org.objectweb.asm.Type;


/**
 * RVshared
 * 实际上继承了继承了AdviceAdapter 继承了一系列后 继承了MethodVisitor
 *
 *
 * 不加 adapter 就是reader 咋进来 writer 咋出去
 * adapter 在 reader 和writer 之间进行修改
 * MethodTransformer 这就是一个 adapter
 *
 * 先 visitor 再 transformer
 *
 * 每个方法都会产生一个MethodTransformer实例
 */
public class MethodTransformer extends AdviceAdapter implements Opcodes {
    final static String logClass = "profile/ProfileRunTime";
    private String className;
    private String methodName;
    private int liNumber;
    //编译器自动生成的类名带init
    private boolean isInit=false;




    private int maxindex_cur;

    String methodSignature;


    /**
     *
     * @param mv 每个 .class 对应的 mv
     * @param access
     * @param className
     * @param name
     * @param desc description 属性 描述符
     */
    protected MethodTransformer(MethodVisitor mv, int access,String className, String name, String desc) {
        super(Opcodes.ASM5, mv, access, name, desc);
        //className传给CLassTransformer
        this.className = className;
        methodName= name;
        if(methodName.equals("<init>")|| methodName.equals("<clinit>")){
            //判断不是构造函数 再进行一些操作
        }
        this.maxindex_cur = Type.getArgumentsAndReturnSizes(desc) + 1;

    }

    //获取行号
    @Override
    public void visitLineNumber(int line, Label start) {
        liNumber=line;
        mv.visitLineNumber(line, start);
    }


    //这个函数用于对字节码文件进行操作
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {

        String sig_var = (owner + "." + name).replace("/", ".");
        String sig_loc = (owner + "|" + methodSignature + "|" + sig_var + "|" + liNumber)
                .replace("/", ".");
        int SID = RVGlobalStateForInstrumentation.instance
                .getVariableId(sig_var);
        int ID = RVGlobalStateForInstrumentation.instance
                .getLocationId(sig_loc);


        //------------------------------------插桩的代码--------------------------------------
        boolean isRead=false;
        if(opcode == GETSTATIC || opcode == GETFIELD){
            //
        }
        switch (opcode){
            case GETSTATIC://读操作的插桩
                if(!isInit){
                    //不是构造函数 再操作
                    insertScheduleNode(mv,isRead,className,methodName,name,liNumber);
                    mv.visitFieldInsn(opcode, owner, name, desc);


                    addBipushInsn(mv, ID);
                    addBipushInsn(mv, SID);
                    addBipushInsn(mv, 0);

                    mv.visitMethodInsn(INVOKESTATIC, logClass,
                            RVConfig.instance.LOG_FIELD_ACCESS,
                            RVConfig.instance.DESC_LOG_FIELD_ACCESS_DETECT_SHARING);

                }
                break;
            case PUTSTATIC://写操作的插桩
                /**
                 * stacks 栈需要的大小
                 * locals 局部变量表大小 除了static最小为1 因为有this
                 * args_size 参数个数
                 */


                /**
                 * a+b=c
                 * getStatic a           getStatic b                         temp=a+b
                 * putStatic c时 temp出栈赋值给c       dup（dumplication）temp出栈
                 *
                 * 要建立一个t1到操作数栈 再存储到局部变量表中 索引为 maxindex_cur++ 然后写插桩的代码
                 */
                if(!isInit){
                    // 不是构造函数 再操作
                    maxindex_cur++;//不覆盖当前栈帧即可 大一些也可以
                    int index=maxindex_cur;
                    //01.将值存入局部变量表 然后操作数栈空了
                    storeValue(desc,index);
                    //02.将局部变量中的值load回局部变量表
                    loadValue(desc,index);
                    //03. 插入调度点
                    insertScheduleNode(mv,isRead,className,methodName,name,liNumber);
                    //04. 插入原来代码
                    mv.visitFieldInsn(opcode, owner, name, desc);

                }
                break;
                default:
                    mv.visitFieldInsn(opcode, owner, name, desc);
                    break;
        }
        //-------------------------------------------------------------------------------------








    }





    private static void insertScheduleNode(MethodVisitor mv, boolean isRead, String className, String methodName, String name, int liNumber){
        /**
         * 每个方法对应一个栈帧
         * 每个栈帧 包括局部变量表 操作数栈
         *
         * Scheduler beforeFieldAccess()
         * invokestatic 五个参数 一个bool 三个string 一个int
         * 五个参数先入栈 调用方法时 五个参数出栈
         *
         */
        // 压栈操作
        mv.visitInsn(isRead ? ICONST_1 : ICONST_0); //是否是读操作   1 true   0 false
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
        mv.visitLdcInsn(name);
        mv.visitLdcInsn(new Integer(liNumber));

        // 插桩 一句完成
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,// 字节码指令
                InstrumentHelper.CLASSRECEIVER,// 接受类
                InstrumentHelper.BEFOREFIELDACCESS,// 对应方法
                InstrumentHelper.BOOL_3String_INT_VOID// 方法描述符
        );
    }


    private void storeValue(String desc, int index) {
        if (desc.startsWith("L") || desc.startsWith("[")) {
            mv.visitVarInsn(ASTORE, index);
        } else if (desc.startsWith("I") || desc.startsWith("B")
                || desc.startsWith("S") || desc.startsWith("Z")
                || desc.startsWith("C")) {
            mv.visitVarInsn(ISTORE, index);  //store value to the #index variable
        } else if (desc.startsWith("J")) {
            mv.visitVarInsn(LSTORE, index);
            maxindex_cur++;
        } else if (desc.startsWith("F")) {
            mv.visitVarInsn(FSTORE, index);
        } else if (desc.startsWith("D")) {
            mv.visitVarInsn(DSTORE, index);
            maxindex_cur++;
        }

        // if(classname.equals("org/eclipse/core/runtime/internal/adaptor/PluginConverterImpl"))
        // System.out.println("Signature: "+desc);
    }



    private void loadValue(String desc, int index) {
        if (desc.startsWith("L") || desc.startsWith("[")) {
            mv.visitVarInsn(ALOAD, index);
        } else if (desc.startsWith("I")) {
            mv.visitVarInsn(ILOAD, index);
        } else if (desc.startsWith("B")) {
            mv.visitVarInsn(ILOAD, index);
        } else if (desc.startsWith("S")) {
            mv.visitVarInsn(ILOAD, index);
        } else if (desc.startsWith("Z")) {
            mv.visitVarInsn(ILOAD, index);
        } else if (desc.startsWith("C")) {
            mv.visitVarInsn(ILOAD, index);
        } else if (desc.startsWith("J")) {
            mv.visitVarInsn(LLOAD, index);
        } else if (desc.startsWith("F")) {
            mv.visitVarInsn(FLOAD, index);
        } else if (desc.startsWith("D")) {
            mv.visitVarInsn(DLOAD, index);
        }
    }


    private void addBipushInsn(MethodVisitor mv, int val) {
        switch (val) {
            case 0:
                mv.visitInsn(ICONST_0);
                break;
            case 1:
                mv.visitInsn(ICONST_1);
                break;
            case 2:
                mv.visitInsn(ICONST_2);
                break;
            case 3:
                mv.visitInsn(ICONST_3);
                break;
            case 4:
                mv.visitInsn(ICONST_4);
                break;
            case 5:
                mv.visitInsn(ICONST_5);
                break;
            default:
                mv.visitLdcInsn(new Integer(val));
                break;
        }
    }
}
