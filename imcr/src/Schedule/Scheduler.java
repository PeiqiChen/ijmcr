package Schedule;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

/**
 * showbByteOutline 自动生成方法描述符
 * bytecode 每个方法调用对应的字节码指令
 * invokeStatic
 * （调用，这个类的，这个方法）
 *
 */

public class Scheduler {
    private static ReentrantLock schedulerStateLock;
    private static HashMap<String,ThreadInfo> livingThreadInfos;
    private static ArrayList<ThreadInfo> pausedThreadInfos;
    private static MCRStrategy mcrStrategy;

    //调度
    static{
        initState();
        Thread scheduleThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //不断循环 不能只判断一次 实时监测
                    schedulerStateLock.lock();
                    try{
                        if(!livingThreadInfos.isEmpty() && !pausedThreadInfos.isEmpty()){
                            ThreadInfo chooseThreadInfo=choose(pausedThreadInfos);
                            pausedThreadInfos.remove(chooseThreadInfo);
                            chooseThreadInfo.getPausingSemaphore().release();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        schedulerStateLock.unlock();
                    }
                }
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.start();
    }
    private static void initState(){
        livingThreadInfos=new HashMap<String, ThreadInfo>();
        pausedThreadInfos=new ArrayList<ThreadInfo>();
        schedulerStateLock = new ReentrantLock();
    }
    /**
     * @param isRead
     * @param className
     * @param methodName
     * @param fieldName
     * @param linumber 在哪一行 linenumber
     */
    public static void beforeFieldAccess(boolean isRead, String className,  String methodName, String fieldName, int linumber){
        Thread currentThread=Thread.currentThread();
        ThreadInfo currentThreadInfo;
        schedulerStateLock.lock();
        try{
            if(!livingThreadInfos.containsKey(currentThread.getName())){
                currentThreadInfo=new ThreadInfo(currentThread);
                livingThreadInfos.put(currentThread.getName(),currentThreadInfo);
                pausedThreadInfos.add(currentThreadInfo);
            } else{
                currentThreadInfo=livingThreadInfos.get(currentThread.getName());
                pausedThreadInfos.add(currentThreadInfo);
            }
        } finally {
            schedulerStateLock.unlock();
        }

        try {
            if(currentThreadInfo!=null){
                // 不为空时 暂停当前线程
                currentThreadInfo.getPausingSemaphore().acquire();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        for(String tName:livingThreadInfos.keySet() ){
//            System.out.println("当前存活线程");
//            System.out.println(livingThreadInfos.get(tName));
//        }
    }




    // 从pausethread中选取一个放行
    public static ThreadInfo choose(ArrayList<ThreadInfo> choices){

//        System.out.println("----------------------------cur pause threadinfo------------------------------");
//        for (ThreadInfo threadInfo : choices){
//            System.out.println(threadInfo);
//        }
//
//        Scanner input =new Scanner(System.in);
//        int choice =input.nextInt();
//        while(choice>choices.size()){
//            System.err.println("index errrrrr");
//            choice=input.nextInt();
//        }
//        return choices.get(choice-1);
        if (choices.isEmpty()) {
            throw new IllegalArgumentException("There has to be at least one choice i.e. objectChoices cannot be empty");
        }
        schedulerStateLock.lock();
        try {

            ThreadInfo choice;
            // 在只有⼀一个选择的时候 就也没有必要再去把选择权交给Strategy
            if (choices.size() == 1) {  //when there is only one thread, it has to execute this thread
                choice = choices.get(0);
                System.out.println("Choose only:" + choice.toString());
            } else{
                System.out.println("has "+ choices.size()+" choices");
                choice= mcrStrategy.choose(choices);
                System.out.println("we chose "+ choice.toString());
            }

            return choice;
        }finally {
            schedulerStateLock.unlock();
        }
    }
}
