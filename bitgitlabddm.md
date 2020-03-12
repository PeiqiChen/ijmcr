```
javap
```



jdk小版本号 5以后不再用 java虚拟机识别class能不能运行 1.6编译的不能运行1.8

```
package test;

public class CUT {
    public static int money=0;

    public static void main(String[] args) throws Exception{
        Thread t1=new Thread(new Runnable() {
            public void run() {
                money=money+100;
            }
        });
        Thread t2 =new Thread(new Runnable() {
            public void run() {
                money=money-100;
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println(money);
    }
}
```



Instrument.Instrumentor

classWriter





Visitsource

## bitgitlab/ddm