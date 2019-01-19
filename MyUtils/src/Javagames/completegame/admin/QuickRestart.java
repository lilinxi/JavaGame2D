package Javagames.completegame.admin;

import Javagames.sound.AudioStream;
import Javagames.sound.SoundEvent;

public class QuickRestart extends SoundEvent {
    public static final String STATE_CLOSED = "closed";
    public static final String STATE_WAITING = "waiting";
    public static final String STATE_RUNNING = "running";
    public static final String EVENT_FIRE = "fire";
    public static final String EVENT_DONE = "done";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_CLOSE = "close";
    private String currentState;

    public QuickRestart(AudioStream audio) {
        super(audio);
        currentState = STATE_CLOSED;
    }

    public void open() {
        put(EVENT_OPEN);
    }

    public void close() {
        put(EVENT_CLOSE);
    }

    public void fire() {
        put(EVENT_FIRE);
    }

    @Override
    protected void processEvent(String event) throws InterruptedException {
        System.out.println("Quick Restart Got: " + event);
        System.out.println("Current State: " + currentState);
        if (currentState == STATE_CLOSED) {
            if (event == EVENT_OPEN) {
                audio.open();
                currentState = STATE_WAITING;
            }
        } else if (currentState == STATE_WAITING) {
            if (event == EVENT_CLOSE) {
                audio.close();
                currentState = STATE_CLOSED;
            }
            if (event == EVENT_FIRE) {
                audio.start();
                currentState = STATE_RUNNING;
            }
        } else if (currentState == STATE_RUNNING) {
            if (event == EVENT_FIRE) {
                audio.restart();
            }
            if (event == EVENT_CLOSE) {
                audio.stop();
                audio.close();
                currentState = STATE_CLOSED;
            }
            if (event == EVENT_DONE) {
                audio.stop();
                currentState = STATE_WAITING;
            }
        }
        System.out.println("New State: " + currentState);
    }

    @Override
    protected void onAudioFinished() {
        super.onAudioFinished();
        put(EVENT_DONE);
    }
}
