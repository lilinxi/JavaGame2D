package Javagames.completegame.state;

import Javagames.completegame.CompleteGame;
import Javagames.util.Matrix3x3f;

import java.awt.*;

public class State {
    protected StateController controller;
    protected CompleteGame app;

    public void setController(StateController controller) {
        this.controller = controller;
        app = (CompleteGame) controller.getAttribute("app");
    }

    protected StateController getController() {
        return controller;
    }

    public void enter() {}

    public void processInput(float delta) {}

    public void updateObjects(float delta) {}

    public void render(Graphics2D g, Matrix3x3f view) {}

    public void exit() {}
}
