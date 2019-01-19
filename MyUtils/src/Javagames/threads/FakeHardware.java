package Javagames.threads;

import java.util.*;

public class FakeHardware {
    //该类假装使用某些硬件
    //
    //注意所有的非阻塞方法是如何产生一个新的线程来完成任务后立即返回的。
    //任务实际在另一个线程中发生，并且随后触发了监听器以通知该任务完成了。
    //
    //FakeHardware是一个非阻塞类，如下方法中的每一个都立即返回，稍后再其他线程中执行任务。
    //1. turnOn()
    //2. turnOff()
    //3. start()
    //4. stop()
    //任何需要花很长时间完成的任务，例如，真正的长文件的加载，复杂的人工智能的计算，或者网络通信，
    //都可以从这种非阻塞的类设计中的到益处。

    private static final int SLEEP_MIN = 100;
    private static final int SLEEP_MAX = 500;

    public enum FakeHardwareEvent {
        START,
        STOP,
        ON,
        OFF,
    }

    private volatile boolean on = false;
    private volatile boolean running = false;
    private String name;
    //需要在多线程中访问的列表应定义为线程安全的列表！！！
    //public static <T> List<T> synchronizedList(List<T> list)返回指定列表支持的同步（线程安全的）列表。
    // 为了保证按顺序访问，必须通过返回的列表完成所有对底层实现列表的访问。
    //在返回的列表上进行迭代时，用户必须手工在返回的列表上进行同步：
    //
    //  List list = Collections.synchronizedList(new ArrayList());
    //      ...
    //  synchronized(list) {
    //      Iterator i = list.iterator(); // Must be in synchronized block
    //      while (i.hasNext())
    //          foo(i.next());
    //  }
    // 不遵从此建议将导致无法确定的行为。
    //如果指定列表是可序列化的，则返回的列表也将是可序列化的。
    private List<FakeHardwareListener> listeners =
            Collections.synchronizedList(new ArrayList<FakeHardwareListener>());

    public FakeHardware(String name) {
        this.name = name;
    }

    public boolean addListener(FakeHardwareListener listener) {
        return listeners.add(listener);
    }

    public boolean isOn() {
        return on;
    }

    public boolean isRunning() {
        return running;
    }

    private void sleep() {
        int rand = new Random().nextInt(SLEEP_MAX - SLEEP_MIN);
        sleep(rand + SLEEP_MIN);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {}
    }

    public void turnOn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOn();
            }
        }).start();
    }

    public void turnOff() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOff();
            }
        }).start();
    }

    public void start(final int timeMS, final int slices) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStart(timeMS, slices);
            }
        }).start();
    }

    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStop();
            }
        }).start();
    }

    private synchronized void setOn() {
        if (!on) {
            on = true;
            fireEvent(FakeHardwareEvent.ON);
        }
    }

    private synchronized void setOff() {
        if (on) {
            setStop();
            on = false;
            fireEvent(FakeHardwareEvent.OFF);
        }
    }

    //setStart()方法有一个问题
    //一旦事件发送了，它调用runTask()方法。
    //该方法不能同步的完成，或者说在任务运行时，没有其他的同步语句块可以调用。
    //然而，由于编写方法代码的方式，它不是线程安全的。
    //如果在同步语句块之后但是下一个Boolean检查之前，硬件停止了，那么任务不会运行，并且也不会发送STOP事件。
    //这是一个有意的bug，即便当类不是线程安全的，我们也必须以线程安全的方式使用该类，而不是修正代码。
    private void setStart(int timeMS, int slices) {
        synchronized (this) {
            if (on && !running) {
                running = true;
                fireEvent(FakeHardwareEvent.START);
            }
            if (running) {
                runTask(timeMS, slices);
                running = false;
                fireEvent(FakeHardwareEvent.STOP);
            }
        }
    }

    private synchronized void setStop() {
        if (running) {
            running = false;
            //don't send the event
            //not actually done yet
        }
    }

    private void runTask(int timeMS, int slices) {
        int sleep = timeMS / slices;
        for(int i=0;i<slices;i++) {
            if (!running) {
                return;
            }
            System.out.println(name + "[" + (i + 1) + "/" + slices + "]");
            sleep(sleep);
        }
    }

    private void fireEvent(FakeHardwareEvent event) {
        //fireEvent()把完成的事件通知所有的监听器
        synchronized (listeners) {
            for (FakeHardwareListener listener : listeners) {
                listener.event(this, event);
            }
        }
    }
}
