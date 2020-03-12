package asm.core;

/**
 * Created by yunshen.ljy on 2015/6/8.
 */
public class Task {

    private int isTask = 0;

    private long tell = 0;

    public void isTask(){
        System.out.println("call isTask");
    }
    public void tellMe(){
        System.out.println("call tellMe");
    }
}