package Javagames.completegame;

import Javagames.completegame.admin.Acme;
import Javagames.completegame.admin.GameConstants;
import Javagames.completegame.admin.QuickLooper;
import Javagames.completegame.admin.QuickRestart;
import Javagames.completegame.object.PolygonWrapper;
import Javagames.completegame.state.GameLoading;
import Javagames.completegame.state.StateController;
import Javagames.sound.LoopEvent;
import Javagames.util.FullScreenFramework;
import Javagames.util.WindowFramework;

import java.awt.*;

public class CompleteGame extends FullScreenFramework {
    private StateController controller;

    public CompleteGame() {
        appBorder = GameConstants.APP_BORDER;
        appWidth = GameConstants.APP_WIDTH;
        appHeight = GameConstants.APP_HEIGHT;
        appSleep = GameConstants.APP_SLEEP;
        appTitle = GameConstants.APP_TITLE;
        appWorldWidth = GameConstants.WORLD_WIDTH;
        appWorldHeight = GameConstants.WORLD_HEIGHT;
        appBorderScale = GameConstants.BORDER_SCALE;
        appDisableCursor = GameConstants.DISABLE_CURSOR;
        appMaintainRatio = GameConstants.MAINTAIN_RATIO;
    }

    @Override
    protected void initialize() {
        super.initialize();
        controller = new StateController();
        controller.setAttribute("app", this);
        controller.setAttribute("keys", keyboard);
        controller.setAttribute("ACME", new Acme(this));
        controller.setAttribute("wrapper", new PolygonWrapper(appWorldWidth, appWorldHeight));
        controller.setAttribute("viewport", getViewportTransform());
        controller.setState(new GameLoading());
    }

    public void shutDownGame() {
        shutDown();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        controller.processInput(delta);
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        controller.updateObjects(delta);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        controller.render((Graphics2D) g, getViewportTransform());
    }

    @Override
    protected void terminate() {
        super.terminate();
        QuickRestart event = (QuickRestart) controller.getAttribute("fire-clip");
        if (event != null) {
            System.out.println("Sound: fire-clip");
            event.close();
            event.shutDown();
            System.out.println("Done: fire-clip");
        }
        LoopEvent loop = (LoopEvent) controller.getAttribute("ambience");
        if (loop != null) {
            System.out.println("Sound: ambience");
            loop.done();
            loop.shutDown();
            System.out.println("Done: ambience");
        }
        QuickRestart[] explosions = (QuickRestart[]) controller.getAttribute("explosions");
        for(int i=0;i<explosions.length;i++) {
            System.out.println("Sound explosions: " + i);
            explosions[i].close();
            explosions[i].shutDown();
            System.out.println("Done: explosions");
        }
        QuickLooper thruster = (QuickLooper) controller.getAttribute("thruster-clip");
        if (thruster != null) {
            System.out.println("Sound: thruster-clip");
            thruster.close();
            thruster.shutDown();
            System.out.println("Done: thruster-clip");
        }
    }

    public static void main(String[] args) {
        launchApp(new CompleteGame());
    }
}
