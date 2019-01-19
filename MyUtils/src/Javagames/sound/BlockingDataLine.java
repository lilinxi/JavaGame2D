package Javagames.sound;

public class BlockingDataLine extends AudioStream {
    //阻塞类
    //扩展了AudioStream类，并提供了抽象方法的实现。
    //包装了AudioDataLine对象，本质上使用SourceDataLine播放音频。
    //
    private AudioDataLine stream;

    public BlockingDataLine(byte[] soundData) {
        super(soundData);
    }

    @Override
    public void open() {
        lock.lock();
        try {
            stream = new AudioDataLine(soundData);
            stream.initialize();
            stream.addLineListener(this);
            stream.open();
            while (open) {
                cond.await();
            }
            //UPDATES
            createControls(stream.getLine());
            //UPDATES
            System.out.println("dataLine open");
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
            stream.start();
            while (!started) {
                cond.await();
            }
            System.out.println("dataLine started");
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
            stream.loop(count);
            while (!started) {
                cond.await();
            }
            System.out.println("dataLine started");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        stream.reset();
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            stream.stop();
            while (started) {
                cond.await();
            }
            System.out.println("dataLine stopped");
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
            stream.close();
            while (open) {
                cond.await();
            }
            //UPDATES
            clearControls();
            //UPDATES
            System.out.println("dataLine closed");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
