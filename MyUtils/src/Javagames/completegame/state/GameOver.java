package Javagames.completegame.state;

import Javagames.completegame.object.Asteroid;
import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;

import java.awt.*;
import java.util.List;

public class GameOver extends AttractState {
    GameState state;

    public GameOver(List<Asteroid> asteroids, GameState state) {
        super(asteroids);
        this.state = state;
    }

    @Override
    protected float getWaitTime() {
        return 3.0f;
    }

    @Override
    public AttractState getState() {
        if (highScoreMgr.newHighScore(state)) {
            return new EnterHighScoreName(state);
        } else {
            return new HighScore();
        }
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        super.render(g, view);
        acme.drawScore(g, state.getScore());
        Utility.drawCenteredString(g, app.getScreenWidth(), app.getScreenHeight() / 3, "G A M E O V E R");
    }
}
