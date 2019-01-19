package Javagames.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioDataLine implements Runnable {
    //包装了一个SourceDataLine，并且提供了和Clip相似的类
    private static final int BUFFER_SIZE_MS = 50;//缓冲的大小
    private List<LineListener> lineListeners =
            Collections.synchronizedList(new ArrayList<LineListener>());
    private Thread writer;
    private AudioFormat audioFormat;
    private SourceDataLine dataLine;
    private byte[] rawData;
    private byte[] soundData;
    private int bufferSize;
    private int loopCount;
    private volatile boolean restart = false;

    public AudioDataLine(byte[] rawData) {
        this.rawData = rawData;
    }

    public void initialize() {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(rawData);
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            audioFormat = ais.getFormat();
            bufferSize = computeBufferSize(BUFFER_SIZE_MS);
            soundData = readSoundData(ais);
        } catch (UnsupportedAudioFileException ex) {
            throw new SoundException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new SoundException(ex.getMessage(), ex);
        }
    }

    private int computeBufferSize(int milliseconds) {
        //由缓冲大小，帧速率，
        double sampleRate = audioFormat.getSampleRate();
        double bitSize = audioFormat.getSampleSizeInBits();
        double channels = audioFormat.getChannels();
        System.out.println("Sample Rate: " + sampleRate);
        System.out.println("Bit Size: " + bitSize);
        System.out.println("Channels: " + channels);
        System.out.println("Milliseconds: " + milliseconds);
        if (sampleRate == AudioSystem.NOT_SPECIFIED ||
                bitSize == AudioSystem.NOT_SPECIFIED ||
                channels == AudioSystem.NOT_SPECIFIED) {
            System.out.println("BufferSize: " + -1);
            return -1;
        } else {
            double temp = milliseconds;
            double frames = sampleRate * temp / 1000.0;
            //public static double floor(double a)
            // 返回最大的（最接近正无穷大）double 值，该值小于等于参数，并等于某个整数。
            //如果帧速率不能平均的除开的话，还需要进行调整。
            while (frames != Math.floor(frames)) {
                temp++;
                System.out.println("Milliseconds: " + temp + " change");
                frames = sampleRate * temp / 1000;
            }
            double bytesPerFrame = bitSize / 8.0;
            double size = (int) (frames * bytesPerFrame * channels);
            System.out.println("BufferSize: " + size);
            return (int) size;
        }
    }

    private byte[] readSoundData(AudioInputStream ais) {
        try {
            //需要对原始声音数据进行处理，每次读取一帧
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //public int getFrameSize()获取以字节为单位的帧大小
            long chunk = audioFormat.getFrameSize();
            byte[] buf = new byte[(int) chunk];
            // int read(byte[] b)
            //          从音频输入流读取一定数量的字节，并将其存储在缓冲区数组 b 中。
            while (ais.read(buf) != -1) {
                out.write(buf);
            }
            ais.close();
            return out.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addLineListener(LineListener lineListener) {
        lineListeners.add(lineListener);
    }

    public void open() {
        try {
            //创建SourceDataLine，并且添加所有的LineListener。
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class,
                    audioFormat,
                    AudioSystem.NOT_SPECIFIED//public static final int NOT_SPECIFIED代表未知数字值的整数。
            );
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            synchronized (lineListeners) {
                for (LineListener lineListener : lineListeners) {
                    //事件监听器由SourceDataLine处理
                    dataLine.addLineListener(lineListener);
                }
            }
            dataLine.open(audioFormat, bufferSize);
        } catch (LineUnavailableException ex) {
            throw new SoundException(ex.getMessage(), ex);
        }
    }

    public void close() {
        dataLine.close();
    }

    public void start() {
        //SourceDataLine开始，线程开始
        loopCount = 0;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public void reset() {
        restart = true;
    }

    public void loop(int count) {
        loopCount = count;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public void stop() {
        //结束线程
        if (writer != null) {
            Thread temp = writer;
            writer = null;
            try {
                temp.join(10000);
            } catch (InterruptedException ex) {
            }
        }
    }

    public Line getLine() {
        return dataLine;
    }

    @Override
    public void run() {
        System.out.println("dataLine begin write stream");
        try {
            while (true) {
                int written = 0;
                int length =
                        bufferSize == -1 ? dataLine.getBufferSize() : bufferSize;
                while (written < soundData.length) {
                    if (Thread.currentThread() != writer) {
                        System.out.println("dataLine stream canceled");
                        loopCount = 0;
                        break;
                    } else if (restart) {
                        restart = false;
                        System.out.println("dataLine stream canceled, need to be restarted");
                        if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                            loopCount++;
                        }
                        break;
                    }
                    //int write(byte[] b,
                    //          int off,
                    //          int len)通过此源数据行将音频数据写入混频器。
                    // 所请求的数据字节数是从指定的数组中读取的（从数组中给定的偏移量开始），
                    // 并且将被写入数据行的缓冲区。如果调用者试图写入多于当前可写入数据量的数据（参见 available），
                    // 则此方法在写入所请求数据量之前一直阻塞。
                    // 即使要写入的请求数据量大于数据行的缓冲区大小，此方法也适用。
                    // 不过，如果在写入请求的数据量之前数据行已关闭、停止或刷新，则该方法不再阻塞，
                    // 但它会返回至今为止写入的字节数。
                    int bytesLeft = soundData.length - written;
                    int toWrite = bytesLeft > length * 2 ? length : bytesLeft;
                    written += dataLine.write(soundData, written, toWrite);
                }
                if (loopCount == 0) {
                    break;
                } else if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                    loopCount--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("dataLine stream finished");
            dataLine.drain();
            dataLine.stop();
        }
    }
}
