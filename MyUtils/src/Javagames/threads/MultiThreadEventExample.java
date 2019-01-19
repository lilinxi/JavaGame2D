package Javagames.threads;

import Javagames.util.SimpleFramework;
import Javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MultiThreadEventExample extends SimpleFramework {
    //这是一个简单的游戏循环，使用了3种不同的状态类。
    //每个状态机都在自己的线程中运行，使用阻塞队列沟通来自游戏线程中的事件。
    private OneShotEvent oneShotEvent;
    private LoopEvent loopEvent;
    private RestartEvent restartEvent;

    public MultiThreadEventExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Multi-Thread Event Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        oneShotEvent = new OneShotEvent(5000, 10);
        oneShotEvent.initialize();
        loopEvent = new LoopEvent(1000, 4);
        loopEvent.initialize();
        restartEvent = new RestartEvent(5000, 10);
        restartEvent.initialize();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            oneShotEvent.fire();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            oneShotEvent.done();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            loopEvent.fire();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_4)) {
            loopEvent.done();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_5)) {
            restartEvent.fire();
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        textPos = Utility.drawString(g, 20, textPos,
                "",
                "(1) Fire One Shot",
                "(2) Cancel One Shot",
                "(3) Start Loop",
                "(4) Stop Loop",
                "(5) Reusable");
    }

    @Override
    protected void terminate() {
        super.terminate();
        oneShotEvent.shutDown();
        loopEvent.shutDown();
        restartEvent.shutDown();
    }

    public static void main(String[] args) {
        launchApp(new MultiThreadEventExample());
    }
}

