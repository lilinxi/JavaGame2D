package Javagames.threads;

public class WaitNotifyExample implements FakeHardwareListener {
    //创建一个非阻塞类的阻塞版本
    //使用非阻塞的FakeHardware
    //使用wait()和notify()方法
    //
    //关于阻塞，非阻塞
    //
    //简单点说:
    //
    //阻塞就是干不完不准回来，
    //非阻塞就是你先干，我现看看有其他事没有，完了告诉我一声
    //
    //我们拿最常用的send和recv两个函数来说吧...
    //比如你调用send函数发送一定的Byte,
    // 在系统内部send做的工作其实只是把数据传输(Copy)到TCP/IP协议栈的输出缓冲区,
    // 它执行成功并不代表数据已经成功的发送出去了,
    // 如果TCP/IP协议栈没有足够的可用缓冲区来保存你Copy过来的数据的话...
    // 这时候就体现出阻塞和非阻塞的不同之处了:
    // 对于阻塞模式的socket send函数将不返回直到系统缓冲区有足够的空间把你要发送的数据Copy过去以后才返回,
    // 而对于非阻塞的socket来说send会立即返回WSAEWOULDDBLOCK告诉调用者说:"发送操作被阻塞了!!!你想办法处理吧..."
    //对于recv函数,同样道理,该函数的内部工作机制其实是在等待TCP/IP协议栈的接收缓冲区通知它说:
    // 嗨,你的数据来了.对于阻塞模式的socket来说如果TCP/IP协议栈的接收缓冲区没有通知一个结果给它它就一直不返回:
    // 耗费着系统资源....
    // 对于非阻塞模式的socket该函数会马上返回,然后告诉你:WSAEWOULDDBLOCK---"现在没有数据,回头在来看看"
    //
    //阻塞
    //    阻塞调用是指调用结果返回之前，当前线程会被挂起。函数只有在得到结果之后才会返回。
    // 有人也许会把阻塞调用和同步调用等同起来，实际上它们是不同的。
    // 对于同步调用来说，很多时候当前线程还是激活的，只是从逻辑上当前函数没有返回而已。
    // 例如，我们在CSocket中调用Receive函数，如果缓冲区中没有数据，这个函数就会一直等待，直到有数据才返回。
    // 而此时，当前线程还会继续处理各种各样的消息。如果主窗口和调用函数在同一个线程中，
    // 除非你在特殊的界面操作函数中调用，其实主界面还是应该可以刷新。
    // socket接收数据的另外一个函数recv则是一个阻塞调用的例子。
    // 当socket工作在阻塞模式的时候， 如果没有数据的情况下调用该函数，则当前线程就会被挂起，直到有数据为止。
    //
    //非阻塞
    //    非阻塞和阻塞的概念相对应，指在不能立刻得到结果之前，该函数不会阻塞当前线程，而会立刻返回。
    //
    //对象的阻塞模式和阻塞函数调用
    //
    //    对象是否处于阻塞模式和函数是不是阻塞调用有很强的相关性，但是并不是一一对应的。
    // 阻塞对象上可以有非阻塞的调用方式，我们可以通过一定的API去轮询状态，在适当的时候调用阻塞函数，就可以避免阻塞。
    // 而对于非阻塞对象，调用特殊的函数也可以进入阻塞调用。函数select就是这样的一个例子。
    //
    public WaitNotifyExample() {}

    //另一个线程的任务没有完成时进行等待，任务完成后会发送事件，接收到事件之后结束等待
    public void runTest()throws Exception {
        FakeHardware hardware = new FakeHardware("name");
        hardware.addListener(this);
        synchronized (this) {
            hardware.turnOn();
            while (!hardware.isOn()) {
                wait();
            }
        }
        System.out.println("Hardware is on!");
        synchronized (this) {
            hardware.start(1000, 4);
            while (!hardware.isRunning()) {
                wait();
            }
        }
        System.out.println("Hardware is running!");
        synchronized (this) {
            while (hardware.isRunning()) {
                wait();
            }
        }
        System.out.println("Hardware is stopped!");
        synchronized (this) {
            hardware.turnOff();
            while (hardware.isOn()) {
                wait();
            }
        }
    }

    @Override
    public synchronized void event(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
        System.out.println("Got Event: " + event);
        notifyAll();
    }

    public static void main(String[] args)throws Exception {
        new WaitNotifyExample().runTest();
    }
}
