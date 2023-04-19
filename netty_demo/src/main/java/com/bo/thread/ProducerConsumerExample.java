package com.bo.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProducerConsumerExample {
    private static final int N_PRODUCERS = 2;
    private static final int N_CONSUMERS = 3;

    public static void main(String[] args) {
        // 创建一个有界队列（容量为10）
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                N_PRODUCERS + N_CONSUMERS, // 核心线程数
                N_PRODUCERS + N_CONSUMERS, // 最大线程数
                0L, TimeUnit.MILLISECONDS, // 空闲线程存活时间
                new LinkedBlockingQueue<>()); // 工作队列

        // 创建生产者和消费者线程并提交到线程池中
        for (int i = 0; i < N_PRODUCERS; i++) {
            executor.execute(new Producer(queue));
        }
        for (int i = 0; i < N_CONSUMERS; i++) {
            executor.execute(new Consumer(queue));
        }

        // 关闭线程池
        executor.shutdown();
    }
}

class Producer implements Runnable {
    private final ArrayBlockingQueue<Integer> queue;

    public Producer(ArrayBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int num = produce();
                queue.put(num);
                System.out.println("Produced: " + num);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int produce() {
        return (int) (Math.random() * 100);
    }
}

class Consumer implements Runnable {
    private final ArrayBlockingQueue<Integer> queue;

    public Consumer(ArrayBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int num = queue.take();
                consume(num);
                System.out.println("Consumed: " + num);
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void consume(int num) {
        // 消费数据的代码
    }
}
