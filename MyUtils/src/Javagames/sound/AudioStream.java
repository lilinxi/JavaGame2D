package Javagames.sound;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AudioStream implements LineListener {
    //定制声音库的基类
    //提供了
    //    public abstract void open();
    //
    //    public abstract void close();
    //
    //    public abstract void start();
    //
    //    public abstract void loop(int count);
    //
    //    public abstract void restart();
    //
    //    public abstract void stop();
    //
    //实现了LineListener接口
    //
    //创建阻塞类，在返回之前等待声音库事件。
    //这里使用一个阻塞类包装一个非阻塞类的技术，即wait/notify方法，或者使用lock/condition
    //
    //AudioStream是用于阻塞音频流的一个基类，包装了Clip和SourceDataLine。
    //使用Lock和Condition变量来监控LineListener。
    //还管理了BlockingAudioListener接口的监听器，并且提供方法来开始，停止，打开，关闭，重新开始以及循环声音。
    //LineListener包含的代码，可以在声音完成时触发事件，并且根据条件变量标记所有的线程等待。
    //
    /**更新
     * 添加声音控件
     * MASTER_GAIN控件将调整音量
     * PAN控件将确定哪个扬声器的声音可以听到
     * 如果声音不是单声道声音，而是一个立体声，使用BALANCE控件
     * 更新后的AudioStream添加了对GAIN和PAN控件的支持
     */
    public static final int LOOP_CONTINUOUSLY = -1;
    protected final Lock lock = new ReentrantLock();
    protected final Condition cond = lock.newCondition();
    protected volatile boolean open = false;
    protected volatile boolean started = false;
    //UPDATES
    //
    //FloatControl 对象提供对浮点值范围的控制。
    // Float 控件常常通过滑块或旋钮之类的可连续调节对象在图形用户界面中表示。
    // FloatControl 的具体子类实现诸如 gain 和 pan 之类的控件，
    // 这些控件会以应用程序可以操作的方式影响行的音频信号
    protected FloatControl gainControl;
    protected FloatControl panControl;
    //
    //UPDATES
    protected byte[] soundData;
    //事件监听器确保线程安全
    private List<BlockingAudioListener> listeners =
            Collections.synchronizedList(new ArrayList<BlockingAudioListener>());

    public AudioStream(byte[] soundData) {
        this.soundData = soundData;
    }

    public abstract void open();

    public abstract void close();

    public abstract void start();

    public abstract void loop(int count);

    public abstract void restart();

    public abstract void stop();

    public boolean addListener(BlockingAudioListener listener) {
        return listeners.add(listener);
    }

    //通知所有的BlockingAudioListener声音已经被播放完成
    protected void fireTaskFinished() {
        synchronized (listeners) {
            for (BlockingAudioListener listener : listeners) {
                listener.audioFinished();
                System.out.println("clip finished");
            }
        }
    }

    @Override
    public void update(LineEvent lineEvent) {
        boolean wasStarted = started;
        lock.lock();
        try {
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
            cond.signalAll();
        }finally {
            lock.unlock();
        }
        if (wasStarted && !started) {
            fireTaskFinished();
        }
    }

    //UPDATES
    //
    public void clearControls() {
        gainControl = null;
        panControl = null;
    }

    public void createControls(Line line) {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gainControl =
                    (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (line.isControlSupported(FloatControl.Type.PAN)) {
            panControl =
                    (FloatControl) line.getControl(FloatControl.Type.PAN);
        }
    }

    public boolean hasGainControl() {
        return gainControl != null;
    }

    public void setGain(float fGain) {
        if (hasGainControl()) {
            gainControl.setValue(fGain);
        }
    }

    public float getGain() {
        return hasGainControl() ? gainControl.getValue() : 0.0f;
    }

    public float getMaximum() {
        return hasGainControl() ? gainControl.getMaximum() : 0.0f;
    }

    public float getMinimum() {
        return hasGainControl() ? gainControl.getMinimum() : 0.0f;
    }

    public boolean hasPanControl() {
        return panControl != null;
    }

    // float getPrecision()
    //          获得该控件的分辨率或粒度，以该控件测量的单位为准。
    public float getPrecision() {
        return hasPanControl() ? panControl.getPrecision() : 0.0f;
    }

    public float getPan() {
        return hasPanControl() ? panControl.getValue() : 0.0f;
    }

    public void setPan(float pan) {
        if (hasPanControl()) {
            panControl.setValue(pan);
        }
    }
    //
    //UPDATES
}
