package Javagames.tools;

import Javagames.util.Utility;
import Javagames.util.Vector2f;
import Javagames.util.WindowFramework;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class ParticleExample extends WindowFramework {
    /**
     * 它展示了粒子的创建，并将其渲染为爆炸的样子。
     * initialize（）方法为圆形爆炸创建了随机颜色的一个列表，以及粒子颜色的一个列表。
     * getRandomPosition（）方法为粒子事件生成随机位置。
     * createParticleRings（）方法创建带有速率和生命周期的5个环。这个爆炸用于之后游戏中玩家的飞船。
     * createRandomParticle（）方法创建了在所有方向上的爆炸。用于之后太空火箭的爆炸。
     * updateObject（）方法生成了随机的粒子事件，绘制每个粒子，并且如果粒子已经死了，则将其从列表移除。
     * 最后，render（）方法遍历粒子列表，并且绘制每个单个粒子。
     */
    private float elapsedRing;
    private float ringTime;
    private float elapsedBurst;
    private float burstTime;
    private Random random;
    private Vector<Color> colors;
    private Vector<TestParticle> particles;

    public ParticleExample() {
        appWidth = 640;
        appHeight = 480;
        appSleep = 1L;
        appTitle = "Particle Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        random = new Random();
        colors = new Vector<Color>();
        particles = new Vector<TestParticle>();
        colors.add(Color.WHITE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        colors.add(Color.PINK);
    }

    private Vector2f getRandomPosition() {
        float x = -0.5f + random.nextFloat();
        float y = -0.5f + random.nextFloat();
        return new Vector2f(x, y);
    }

    private void createParticleRings() {
        for(int ring=0;ring<5;ring++) {
            float velocity = 0.25f + random.nextFloat() * 1.0f;
            float lifeSpan = 1.0f + random.nextFloat() * 1.0f;
            float radius = 0.003f + random.nextFloat() * 0.003f;
            for(int i=0;i<100;i++) {
                TestParticle p = new TestParticle();
                p.setPosition(new Vector2f());
                p.setRadius(radius);
                p.setLifeSpan(lifeSpan);
                p.setColor(colors.get(random.nextInt(colors.size())));
                p.setVector((float) Math.toRadians(random.nextInt(360)), velocity);
                particles.add(p);
            }
        }
    }

    private TestParticle createRandomParticle(Vector2f pos) {
        TestParticle p = new TestParticle();
        p.setPosition(pos);
        p.setRadius(0.002f + random.nextFloat() * 0.004f);
        p.setLifeSpan(random.nextFloat() * 1.0f);
        switch (random.nextInt(4)) {
            case 0:
                p.setColor(Color.WHITE);
                break;
            case 1:
                p.setColor(Color.GRAY);
                break;
            case 2:
                p.setColor(Color.LIGHT_GRAY);
                break;
            case  3:
                p.setColor(Color.DARK_GRAY);
                break;
        }
        float angle = (float) Math.toRadians(random.nextInt(360));
        float velocity = random.nextFloat() * 2.0f;
        p.setVector(angle, velocity);
        return p;
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        elapsedBurst += delta;
        elapsedRing += delta;
        if (elapsedBurst > burstTime) {
            Vector2f pos = getRandomPosition();
            for(int i=0;i<150;i++) {
                particles.add(createRandomParticle(pos));
            }
            elapsedBurst = 0.0f;
            burstTime = 0.1f + random.nextFloat() * 1.5f;
        }
        if (elapsedRing > ringTime) {
            elapsedRing = 0.0f;
            createParticleRings();
            ringTime = 2.5f + random.nextFloat() * 2.5f;
        }
        Iterator<TestParticle> it = particles.iterator();
        while (it.hasNext()) {
            TestParticle p = it.next();
            p.update(delta);
            if (p.hasDied()) {
                it.remove();
            }
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Utility.drawString(g, 20, textPos, "burstTime:" + burstTime, "ringTime:" + ringTime);
        Utility.drawRect(g, new Vector2f(), new Vector2f(getScreenWidth() - 1, getScreenHeight() - 1), Color.GREEN);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        for (TestParticle particle : particles) {
            particle.draw(g2d, getViewportTransform());
        }
    }

    public static void main(String[] args) {
        launchApp(new ParticleExample());
    }
}
