package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MathGame {
    private static Random random = new Random();

    private int illegalArgumentCount = 0;

    public static void main(String[] args) throws InterruptedException {
        MathGame game = new MathGame();
        //模拟死锁
        //deadThreadFun();

        //模拟高cpu
        //highCPU();

        while (true) {
            game.run();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void run() throws InterruptedException {
        try {
            int number = random.nextInt() / 10000;
            performance(number % 5);
            List<Integer> primeFactors = primeFactors(number, ParamInfo.newRandomParamInfo());
            print(number, primeFactors);

        } catch (Exception e) {
            System.out.println(String.format("illegalArgumentCount:%3d, ", illegalArgumentCount) + e.getMessage());
        }
    }

    public static void print(int number, List<Integer> primeFactors) {
        StringBuffer sb = new StringBuffer(number + "=");
        for (int factor : primeFactors) {
            sb.append(factor).append('*');
        }
        if (sb.charAt(sb.length() - 1) == '*') {
            sb.deleteCharAt(sb.length() - 1);
        }
        System.out.println(sb);
    }

    public List<Integer> primeFactors(int number, ParamInfo paramInfo) {
        System.out.println("number:" + number + ", paramInfo:" + paramInfo);
        if (number < 2) {
            illegalArgumentCount++;
            System.out.println("IllegalArgumentException, number:" + number);
            throw new IllegalArgumentException("number is: " + number + ", need >= 2");
        }

        List<Integer> result = new ArrayList<Integer>();
        int i = 2;
        while (i <= number) {
            if (number % i == 0) {
                //System.out.println("number%i==0,number:" + number);
                result.add(i);
                number = number / i;
                i = 2;
            } else {
                i++;
                //System.out.println("number %i!=0");
            }
        }

        if (number % 5 == 1) {
            System.out.println("sleep...");
            sleepSilence(20);
        }
        return result;
    }

    private static void sleepSilence(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static class ParamInfo {
        private static Random random = new Random();

        private int age;
        private String name;
        private List<Integer> list;

        public ParamInfo(int age, String name, List<Integer> list) {
            this.age = age;
            this.name = name;
            this.list = list;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }

   /* @Override
    public String toString() {
        return "ParamInfo{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", list=" + list +
                '}';
    }*/

        public static ParamInfo newRandomParamInfo() {
            int age = Math.abs(random.nextInt()) % 10;
            List<Integer> list = new ArrayList<Integer>();
            for (int i = age; i >= 0; i--) {
                list.add(Integer.valueOf(i));
            }

            return new ParamInfo(age, "name_" + age, list);
        }
    }


    /**
     * 创建死锁的方法
     */
    private static void deadThreadFun() {
        Object resource1 = new Object();
        Object resource2 = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resource1) {
                System.out.println("already get resource1, current thread:" + Thread.currentThread());
                sleepSilence(1000);
                System.out.println("waiting to get resource2, current thread:" + Thread.currentThread());
                synchronized (resource2) {
                    System.out.println("get resource 2, current thread:" + Thread.currentThread());
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resource2) {
                System.out.println("already get resource2, current thread:" + Thread.currentThread());
                sleepSilence(1000);
                System.out.println("waiting to get resource1, current thread:" + Thread.currentThread());
                synchronized (resource1) {
                    System.out.println("get resource 1, current thread:" + Thread.currentThread());
                }
            }
        });

        thread1.start();
        thread2.start();
    }


    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * 模拟高cpu
     */
    private static void highCPU() {
        Runnable runnable = () -> {
            while (true) {
                System.out.println("high CPU running...");
            }
        };

        executorService.submit(runnable);
    }


    public void performance(int value) {
        System.out.println("performance value:" + value);
        sleepSilence(10);
    }
}
