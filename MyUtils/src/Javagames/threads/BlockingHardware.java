package Javagames.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingHardware {
    //这是一个用阻塞版本包装FakeHardware类的一个类，
    //使用Lock和Condition类，这两个类是并发库的一部分，提供了一些wait/notify没有的功能。
    //Lock + Condition相当于synchronized + wait + notify
    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private volatile boolean on = false;
    private volatile boolean started = false;
    private FakeHardware hardware;
    private List<BlockingHardwareListener> listeners
            = new ArrayList<BlockingHardwareListener>();

    public BlockingHardware(String name) {
        hardware = new FakeHardware(name);
        hardware.addListener(new FakeHardwareListener() {
            @Override
            public void event(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
                handleHardwareEvent(source, event);
            }
        });
    }

    public boolean addListener(BlockingHardwareListener listener) {
        return listeners.add(listener);
    }

    public void start(int ms, int slices) {
        //等待一个对象
        lock.lock();
        try {
            hardware.start(ms, slices);
            while (!started) {
                cond.await();
            }
            System.out.println("It's started");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            hardware.stop();
            while (started) {
                cond.await();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void turnOn() {
        lock.lock();
        try {
            hardware.turnOn();
            while (!on) {
                cond.await();
            }
            System.out.println("Turned on");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void turnOff() {
        lock.lock();
        try {
            hardware.turnOff();
            while (on) {
                cond.await();
            }
            System.out.println("Turned off");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    protected void handleHardwareEvent(
            FakeHardware source, FakeHardware.FakeHardwareEvent event
    ) {
        boolean wasStarted = started;
        lock.lock();
        try {
            switch (event) {
                case ON:
                    on = true;
                    break;
                case OFF:
                    on = false;
                    break;
                case START:
                    started = true;
                    break;
                case STOP:
                    started = false;
                    break;
            }
            //处理完事件后不再等待
            cond.signalAll();
        }finally {
            lock.unlock();
        }
        if (wasStarted && !started) {
            fireTaskFinished();
        }
    }

    private void fireTaskFinished() {
        synchronized (listeners) {
            for (BlockingHardwareListener listener : listeners) {
                listener.taskFinished();
            }
        }
    }
}
