package com.pronetway.customview.test.threadtest;


/**
 * Description:TODO
 * Create Time: 2018/1/8.10:07
 * Author:jin
 * Email:210980059@qq.com
 */
public class Simple2 {
    public static void main(String[] args) {
        QueueBuffer q = new QueueBuffer();
        new Producer(q);
        new Producer(q);
        new Consumer(q);
        new Consumer(q);
        System.out.println("Press Control-C to stop.");
    }

    public static class QueueBuffer {
        int n;
        boolean valueSet = false;
        synchronized int get() {
            System.out.println("Got抢到执行权");
            if (!valueSet)
                try {
                    System.out.println("Got在wait");
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException caught");
                }
            try {
                System.out.println("Got准备睡2s");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Got执行了: " + n);
            valueSet = false;
            notify();
            System.out.println("Got说开抢");
            return n;
        }
        synchronized void put(int n) {
            System.out.println("Put抢到执行权");
            if (valueSet)
                try {
                    System.out.println("put在wait");
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException caught");
                }
            try {
                System.out.println("Put准备睡2s");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.n = n;
            valueSet = true;
            System.out.println("Put执行了: " + n);
            notify();
            System.out.println("Put说开抢");
        }
    }

    public static class Consumer implements Runnable {
        private QueueBuffer q;
        Consumer(QueueBuffer q) {
            this.q = q;
            new Thread(this, "Consumer").start();
        }
        public void run() {
            while (true) {
                q.get();
            }
        }
    }

    public static class Producer implements Runnable {
        private QueueBuffer q;
        Producer(QueueBuffer q) {
            this.q = q;
            new Thread(this, "Producer").start();
        }
        public void run() {
            int i = 0;
            while (true) {
                q.put(i++);
            }
        }
    }
}


