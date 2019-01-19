package Javagames.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class SafeKeyboardInput implements KeyListener {
    //解决帧速率太慢的话，重复的按键事件会被漏掉
    //解决方案：
    //      捕获所有事件，并在游戏循环中遍历它们，从而不管帧速率是多少，都不会漏掉事件
    //将所有接受到的事件存入链表，每次遍历链表处理事件
    enum EventType {
        PRESSED,
        RELEASED,
        TYPED,
    }

    class Event {
        KeyEvent event;
        EventType type;

        public Event(KeyEvent event, EventType type) {
            this.event = event;
            this.type = type;
        }
    }

    private LinkedList<Event> eventThread = new LinkedList<Event>();
    private LinkedList<Event> gameThread = new LinkedList<Event>();
    private Event event = null;
    private int[] polled;

    public SafeKeyboardInput() {
        polled = new int[256];
    }

    public synchronized boolean keyDown(int keyCode) {
        return keyCode == event.event.getKeyCode() && polled[keyCode] > 0;
    }

    public synchronized boolean keyDownOnce(int keyCode) {
        return keyCode == event.event.getKeyCode() && polled[keyCode] == 1;
    }

    public boolean processEvent() {
        // E poll()
        //          获取并移除此列表的头（第一个元素）
        event = gameThread.poll();
        if (event != null) {
            int keyCode = event.event.getKeyCode();
            if (keyCode >= 0 && keyCode < polled.length) {
                if (event.type == EventType.PRESSED) {
                    polled[keyCode]++;
                } else if (event.type == EventType.RELEASED) {
                    polled[keyCode] = 0;
                }
            }
        }
        return event != null;
    }

    public Character getKeyTyped() {
        if (event.type != EventType.TYPED) {
            return null;
        } else {
            return event.event.getKeyChar();
        }
    }

    public synchronized void poll() {
        LinkedList<Event> swap = eventThread;
        eventThread = gameThread;
        gameThread = swap;
    }

    public synchronized void keyPressed(KeyEvent e) {
        eventThread.add(new Event(e, EventType.PRESSED));
    }

    public synchronized void keyReleased(KeyEvent e) {
        eventThread.add(new Event(e, EventType.RELEASED));
    }

    public synchronized void keyTyped(KeyEvent e) {
        eventThread.add(new Event(e, EventType.TYPED));
    }
}
