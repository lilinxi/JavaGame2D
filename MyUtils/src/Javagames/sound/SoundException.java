package Javagames.sound;

public class SoundException extends RuntimeException {
    //定制声音库的基类
    //用作一个定制的异常，扩展了RuntimeException类，以便不需要特别捕获声音API异常。
    //可以直接由message构造，也可以由message+exception构造
    public SoundException(String message) {
        super(message);
    }

    public SoundException(String message, Throwable cause) {
        super(message,cause);
    }
}
