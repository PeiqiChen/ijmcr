package Schedule;

import java.util.concurrent.Semaphore;

public class ThreadInfo {
    private Thread thread;
    private Semaphore pausingSemaphore;
    public ThreadInfo(Thread thread){
        this.thread=thread;
        this.pausingSemaphore=new Semaphore(0);//允许运行的线程数目 初始化为0 即一开始全局暂停
    }
    public Semaphore getPausingSemaphore(){
        return this.pausingSemaphore;
    }

    @Override
    public String toString() {
        return "ThreadInfo{"+
                "thread=" + thread.getName()+
                ",pausingSemaphore="+pausingSemaphore+"}";
    }
}
