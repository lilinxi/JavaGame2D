package Javagames.text;

import Javagames.util.SafeKeyboardFramework;
import Javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SafeKeyboardInputExample extends SafeKeyboardFramework {
    //使用SafeKeyboardInput处理输入
    //绘制字符最后的下划线，每秒闪烁一次，且接收到输入事件时一直保持亮
    //接受控制字符换行和退格
    private long sleepDelta = 100L;
    private int spacesCount;
    private float blink;
    private boolean drawCursor;
    private ArrayList<String> strings = new ArrayList<String>();

    public SafeKeyboardInputExample() {
        appSleep = 10L;
        appTitle = "Safe Keyboard Input Example";
        strings.add("");
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        //使用了新框架的类必须在while循环内处理键盘输入
        while (keyboard.processEvent()) {
            if (keyboard.keyDown(KeyEvent.VK_UP)) {
                appSleep += sleepDelta * delta;
                if (appSleep > 1000L) {
                    appSleep = 1000L;
                }
            }
            if (keyboard.keyDown(KeyEvent.VK_DOWN)) {
                appSleep -= sleepDelta * delta;
                if (appSleep < 0L) {
                    appSleep = 0L;
                }
            }
            if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE)) {
                spacesCount = 0;
            }
            if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
                spacesCount++;
            }
            processTypeChar();
        }
    }

    private void processTypeChar() {
        Character typedChar = keyboard.getKeyTyped();
        if (typedChar != null) {
            //public static boolean isISOControl(char ch)
            // 确定指定字符是否为 ISO 控制字符。
            // 如果字符的代码在从 '\u0000' 到 '\u001F'
            // 或从 '\u007F' 到 '\u009F' 的范围内，则认为该字符是一个 ISO 控制字符。
            if (Character.isISOControl(typedChar)) {
                if (KeyEvent.VK_BACK_SPACE == typedChar) {
                    removeCharacter();
                }
                if (KeyEvent.VK_ENTER == typedChar) {
                    strings.add("");
                }
            } else {
                addCharacter(typedChar);
            }
            drawCursor = true;
            blink = 0.0f;
        }
    }

    private void removeCharacter() {
        String line = strings.remove(strings.size() - 1);
        if (!line.isEmpty()) {
            strings.add(line.substring(0, line.length() - 1));
        }
        if (strings.isEmpty()) {
            strings.add("");
        }
    }

    private void addCharacter(Character c) {
        strings.add(strings.remove(strings.size() - 1) + c);
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        blink += delta;
        if (blink > 0.5f) {
            blink -= 0.5f;
            drawCursor = !drawCursor;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        textPos = Utility.drawString(g, 20, textPos,
                "",
                "Sleep Value: " + appSleep,
                "Space Count: " + spacesCount,
                "Press Up to increase sleep",
                "Press Down to decrease sleep",
                "Press ESC to clear spaces count",
                "",
                "",
                "");
        textPos = Utility.drawString(g, 20, textPos, strings);
        if (drawCursor) {
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
            int y = textPos - height;
            int x = 20 + fm.stringWidth(strings.get(strings.size() - 1));
            g.drawString("_", x, y + fm.getAscent());
        }
    }

    public static void main(String[] args) {
        launchApp(new SafeKeyboardInputExample());
    }
}
