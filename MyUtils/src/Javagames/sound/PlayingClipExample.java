package Javagames.sound;

import Javagames.util.ResourceLoader;

import javax.sound.sampled.*;
import java.io.*;

public class PlayingClipExample implements LineListener {
    //展示了播放声音的正确方式，即等待LineListener事件
    //runTestWithoutWaiting()方法（可能会死锁）展示了使用声音API的错误方式。
    //由于它在继续之前没有等待LineListener事件，因此它可能会死锁，耗尽一个不会播放的声音。
    //runTestWithWaiting()展示了使用wait/notify机制来等待LineListener事件，然后继续进行。
    private volatile boolean open = false;
    private volatile boolean started = false;

    public byte[] readBytes(InputStream in) {
        try {
            BufferedInputStream buf = new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while ((read = buf.read()) != -1) {
                out.write(read);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void runTestWithoutWaiting()throws Exception {
        System.out.println("runTestWithoutWaiting()");
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(this);
        InputStream resource = ResourceLoader.load(
                PlayingClipExample.class,
                "res/assets/sound/WEAPON_scifi_fire_02.wav",
                "/./res/assets/sound/WEAPON_scifi_fire_02.wav"
        );
        byte[] rawBytes = readBytes(resource);
        //原始声音字节包含在ByteArrayInputStream中，并且用于创建一个AudioInputStream，
        // 而不用每次播放声音时从一个InputStream流出数据。
        ByteArrayInputStream in = new ByteArrayInputStream(rawBytes);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
        clip.open(audioInputStream);
        //drain方法阻塞，直到所有可以播放的缓冲数据都已经播放了，并且没有数据留下。
        //该方法用于在停止声音之前播放所有的声音数据。
        //调用stop方法而不先使用drain将会立即切断声音，并且将数据留在缓冲中。
        for(int i=0;i<10;i++) {
            //这段代码开始一个剪辑，然后睡眠等待剪辑激活。此时，剪辑会重新启动。
            // 首先，它会停止，缓缓从中剩下的数据也会被清空，帧位置设置回到开始，然后剪辑开始。
            // 最后，剪辑用完，清空缓冲区。
            clip.start();
            while (!clip.isActive()) {
                Thread.sleep(100);
            }
            clip.stop();
            clip.flush();
            clip.setFramePosition(0);
            clip.start();
            clip.drain();
        }
        clip.close();
    }

    public void runTestWithWaiting()throws Exception {
        System.out.println("runTestWithWaiting()");
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(this);
        InputStream resource = ResourceLoader.load(
                PlayingClipExample.class,
                "res/assets/sound/WEAPON_scifi_fire_02.wav",
                "/./res/assets/sound/WEAPON_scifi_fire_02.wav"
        );
        byte[] rawBytes = readBytes(resource);
        ByteArrayInputStream in = new ByteArrayInputStream(rawBytes);
        //原始的声音字节输入到AudioInputStream
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
        synchronized (this) {
            clip.open(audioInputStream);
            while (!open) {
                wait();
            }
        }
        for(int i=0;i<10;i++) {
            clip.setFramePosition(0);
            synchronized (this) {
                clip.start();
                while (!started) {
                    wait();
                }
            }
            //drain()阻塞，播放完缓冲区的所有声音
            clip.drain();
            synchronized (this) {
                clip.stop();
                while (started) {
                    wait();
                }
            }
        }
        synchronized (this) {
            clip.close();
            while (open) {
                wait();
            }
        }
    }

    @Override
    public synchronized void update(LineEvent lineEvent) {
        System.out.println("Got Event: " + lineEvent.getType());
        LineEvent.Type type = lineEvent.getType();
        if (type == LineEvent.Type.OPEN) {
            open = true;
        } else if (type == LineEvent.Type.START) {
            started = true;
        } else if (type == LineEvent.Type.STOP) {
            started = false;
        } else if (type == LineEvent.Type.CLOSE) {
            open = false;
        }
        notifyAll();
    }

    public static void main(String[] args) throws Exception {
        PlayingClipExample lineListenerExample = new PlayingClipExample();
        lineListenerExample.runTestWithWaiting();
        lineListenerExample.runTestWithoutWaiting();
    }
}
