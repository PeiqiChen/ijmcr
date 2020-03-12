```java
Lock lock = new ReentrantLock();

public int getNext(){
  lock.lock();
  int a = value++;
  lock.unlock();
  return a;
}
```

Lock 需要显示地获取和释放锁，繁琐

Synchronized不需要，简单



**Lock**

代码更灵活，自行管理

可以方便地实现公平性，有现成的已经实现的Lock类

非阻塞的获取锁

能被中断的获取锁

超时获取锁，可以设定获取时间，超时返回



**Volatile**

轻量级锁，被volatile修饰的变量在线程之间可见（一个线程修改了一个变量的值，在另一个线程可以读到修改后的值）

synchronized除了让线程互斥意外，还可以保证线程的可见性

```java
/*
 * 多个线程拿到的是同一把锁
 * 不然保证不了可见性
 */
public class Demo{
  public volatile int a =1;
  //在当前demo对象上加锁
  public synchronized int getA(){
    return a;
  }
  public synchronized void setA(int a){
    try{
      Thread.sleep(2)
    } catch(InterruptedException e){
      e.printStackTrace();
    }
    this.a=a;
  }
  public static void main(String[] args){
    Demo demo = new Demo();
    
    demo.a=10;
     new Thread(new Runnable(){
      @Override
      public void run(){
        System.out.print(demo.a);
      }
    }) .start();
    
    
   
    try {
      Thread.sleep(100);
    } catch(InterruptedException e){
      e.printStackTrace();
    }
    System.out.print(demo.getA());  
      
  }
  
}
```

```java
public class Demo2{
  public volatile boolean run=false;
  
  public static void main(String[] args){
    Demo2 demo2= new Demo2();
    
    new Thread(new Runnable(){
      @Override
      public void run(){
       for(int i=1;i<=10;i++){
          System.err.print("running"+i+"time(s)");
         try {
           Thread.sleep(1000);
         } catch (InterruptedException e){
           e.printStackTrace();
         }
       }
       d.run=true;
      }
    }) .start();
    
    
    new Thread(new Runnable(){
      @Override
      public void run(){
        while(!demo2.run){
          // no action
        }
        System.err.print("Thread2 running")
      }
    }).start();
    
    
    
  }
}
```



volatile底层实现

多了一个Lock指令：在多处理器的系统上，

* 将当前系统处理器缓存行的内容写回到系统内存

* 写回到内存的操作回事的在其他cpu缓存了该内存地址的数据失效



volatile只能保证可见性不能保证原子性

只能保证原子性操作对其他线程可见

volatile可以被synchronized取代，但更轻量