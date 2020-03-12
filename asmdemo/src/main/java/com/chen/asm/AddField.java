package com.chen.asm;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;


/**
 * 添加一个名字为 thisIsNewAddF1 的field
 */
public class AddField extends ClassVisitor {
    String fieldName="thisIsNewAddF1";
    private int acc=Opcodes.ACC_PUBLIC;
    boolean isPresent=false;
    public AddField(ClassVisitor cv){
        super(Opcodes.ASM5,cv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor,
                                   String signature, Object value){
        if(name.equals(this.fieldName)){
            isPresent=true;
        }
        return super.visitField(access,name,descriptor,signature,value);
    }

    @Override
    public void visitEnd(){
        if(!isPresent){
            //没有这个字段
            FieldVisitor fv=this.cv.visitField(acc,fieldName,"I",null,3);
            if (fv != null) {
                fv.visitEnd();
            }
        }
            super.visitEnd();
    }

    public static void main(String[] args) throws IOException{
        ClassWriter classWriter=new ClassWriter(3);
        AddField addField=new AddField(classWriter);
        ClassReader classReader=new ClassReader("com.chen.asm.Student");
        classReader.accept(addField,0);

        File file=new File("org/by/CwAdtest.class");
        String parent=file.getParent();
        File parent1=new File(parent);
        parent1.mkdirs();
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(classWriter.toByteArray());
    }
}
