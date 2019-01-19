package Javagames.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BlockingClip extends AudioStream {
    //阻塞类
    //扩展了AudioStream类，并提供了抽象方法的实现。
    //包装了Clip对象。
    //
    //当Clip对象执行任务时，必须阻塞的每一个方法具有如下的签名：
    /**lock.lock()
     * try{
     *     //use clip
     *     while(not finished){
     *         cond.await();
     *     }
     * }finally{
     *     lock.unlock();
     * }
     */
    /**open方法
     * 创建了一个AudioInputStream，从AudioSystem获取Clip，将其自身添加为LineListener，打开该剪辑并且阻塞，
     * 直到LineListener接口接受LineEvent.OPEN事件。
     */
    /**start方法
     * 使用了flush清楚了任何剩余数据的剪辑，将帧的位置设置为0，开始该Clip，
     * 并且在返回之前等待LineEvent.START事件。
     */
    /**loop方法
     * 类似start方法，但是它使用Clip.loop方法传入一个循环计数。使用AudioStream.LOOP_CONTINUOUSLY标志，
     * 将允许声音永久循环。
     */
    /**stop方法
     * stop方法停止Clip，并且等待LineEvent.STOP事件。它不需要先耗尽该行。
     * Clip类将播放，直到1声音完成然后调用BlockingAudio.audioFinished方法。
     */
    /**restart方法
     * 它覆盖了fireTaskFinish方法，挂起该事件，以便当声音停止并再次开始时，和或受到LineEvent.STOP事件时，
     * fireTaskFinished不会错误地通知监听器任务已经停止，而实际上任务只是重新启动了。
     */
    /**
     * close方法
     * 关闭Clip，并且等待LineEvent.CLOSE事件，然后才返回。
     */
    private Clip clip;
    private boolean restart;

    public BlockingClip(byte[] soundData) {
        super(soundData);
    }

    /**
     * This guy could throw a bunch of exceptions.
     * We're going to wrap them all in a custom exception
     * handler that is a RuntimeException so we don't
     * have to catch and throw all these exceptions.
     */
    @Override
    public void open() {
        lock.lock();
        try {
            //创建Clip对象，并且添加声音原始字节文件
            ByteArrayInputStream in = new ByteArrayInputStream(soundData);
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            clip = AudioSystem.getClip();
            clip.addLineListener(this);
            clip.open(ais);
            while (!open) {
                cond.await();
            }
            //UPDATES
            createControls(clip);
            //UPDATES
            System.out.println("clip open");
        } catch (UnsupportedAudioFileException ex) {
            throw new SoundException(ex.getMessage(), ex);
        } catch (LineUnavailableException ex) {
            throw new SoundException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new SoundException(ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.start();
            while (!started) {
                cond.await();
            }
            System.out.println("clip started");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void loop(int count) {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.loop(count);
            while (!started) {
                cond.await();
            }
            System.out.println("clip start");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        restart = true;
        stop();
        restart = false;
        start();
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            clip.stop();
            while (started) {
                cond.await();
            }
            System.out.println("clip stop");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            clip.close();
            while (open) {
                cond.await();
            }
            clip = null;
            //UPDATES
            clearControls();
            //UPDATES
            System.out.println("clip close");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    protected void fireTaskFinished() {
        if (!restart) {
            super.fireTaskFinished();
        }
    }
}
