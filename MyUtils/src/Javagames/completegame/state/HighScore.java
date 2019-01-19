package Javagames.completegame.state;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;

import java.awt.*;

public class HighScore extends AttractState {
    @Override
    protected AttractState getState() {
        return new GameInformationState();
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        super.render(g, view);
        String[] hs = highScoreMgr.getHighScores();
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.GREEN);
        Utility.drawCenteredString(g, app.getScreenWidth(), app.getScreenHeight() / 3, hs);
    }

    @Override
    protected float getWaitTime() {
        return 7.0f;
    }
}
