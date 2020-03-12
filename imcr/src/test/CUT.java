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

    class Person{
        public int age;
        public Person(int age){
            age=age;
        }
    }
}