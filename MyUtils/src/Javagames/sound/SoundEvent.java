package Javagames.sound;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SoundEvent implements Runnable {
    //提供了一个声音库使用状态机的基类，本质上是一个线程
    public static final String SHUT_DOWN = "shutdown";
    protected AudioStream audio;
    protected BlockingQueue<String> queue;
    private Thread consumer;

    public SoundEvent(AudioStream audio) {
        this.audio = audio;
    }

    public void initialize() {
        audio.addListener(getListener());
        queue = new LinkedBlockingDeque<String>();
        consumer = new Thread(this);
        consumer.start();
    }

    public void put(String event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {}
    }

    public void shutDown() {
        Thread temp = consumer;
        consumer = null;
        try {
            queue.put(SHUT_DOWN);
            temp.join(10000L);
            System.out.println("Event shutdown!!!");
        } catch (InterruptedException e) {}
    }

    @Override
    public void run() {
        while (Thread.currentThread() == consumer) {
            try {
                processEvent(queue.take());
            } catch (InterruptedException e) {}
        }
    }

    protected void processEvent(String event) throws InterruptedException {}

    protected void onAudioFinished() {}

    private BlockingAudioListener getListener() {
        return new BlockingAudioListener() {
            @Override
            public void audioFinished() {
                onAudioFinished();
            }
        };
    }
}
