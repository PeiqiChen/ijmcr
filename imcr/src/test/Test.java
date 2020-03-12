package test;

public class Test {
    public static int money=0;
    public static void main(String[] args) throws Exception{

//        System.out.println("test");
        Thread t1=new Thread(new Runnable() {
            public void run() {
                System.out.println("t1");
            }
        });
        Thread t2 =new Thread(new Runnable() {
            public void run() {
                System.out.println("t2");
            }
        });
        Thread t3 =new Thread(new Runnable() {
            public void run() {
                System.out.println("t3");
            }
        });
        Thread t4 =new Thread(new Runnable() {
            public void run() {
                System.out.println("t4");
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

    }
}
