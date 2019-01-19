package Javagames.completegame.admin;

import Javagames.completegame.state.GameState;
import Javagames.completegame.state.Score;
import Javagames.util.XMLUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Vector;

public class HighScoreMgr {
    private static final String HIGH_SCORE_FILE_NAME = "spacerocks.dat";
    private static final String[][] DEFAULT_SCORES = {
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"},
            {"null", "0"}
    };
    private Vector<Score> scores = new Vector<Score>();
    private NumberFormat format = null;

    public HighScoreMgr() {
        format = new DecimalFormat();
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        format.setGroupingUsed(true);
        format.setParseIntegerOnly(true);
    }

    public void loadHighScores() {
        try {
            File file = getHighScoreFile();
            if (!file.exists()) {
                createDefaultFile(file);
            }
            parseHighScoreFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            handleFileError();
        }
    }

    private File getHighScoreFile() {
        return new File(System.getProperty("user.home"), HIGH_SCORE_FILE_NAME);
    }

    private void createDefaultFile(File file)throws IOException {
        if (!file.createNewFile()) {
            throw new IOException();
        }
        String xml = "<hs>\n";
        for (String[] score : DEFAULT_SCORES) {
            xml += "<player name=\"" + score[0] + "\" score=\"" + score[1] + "\" />\n";
        }
        xml += "</hs>";
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.write(xml);
        out.flush();
        out.close();
    }

    private void parseHighScoreFile(File file) {
        try {
            Document document = XMLUtility.parseDocument(new FileReader(file));
            Element root = document.getDocumentElement();
            for (Element player : XMLUtility.getElements(root, "player")) {
                parsePlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePlayer(Element player) {
        String name = player.getAttribute("name");
        int score = Integer.parseInt(player.getAttribute("score"));
        scores.add(new Score(name, score));
    }

    private void handleFileError() {
        JOptionPane.showMessageDialog(
                null,
                "Could not create High File" +
                        "\nWill not be able to save high scores",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        for (String[] score : DEFAULT_SCORES) {
            scores.add(
                    new Score(score[0], Integer.parseInt(score[1]))
            );
        }
    }

    private String getFormattedScore(int index, Score score) {
        String str = "" + (index + 1) + ". " + score.name;
        str += " - " + format(score.score);
        return str;
    }

    public String format(int score) {
        return format.format(score);
    }

    public String[] getHighScores() {
        Vector<String> hs = new Vector<String>();
        hs.add("H I G H S C O R E S");
        hs.add("-------------------------");
        for(int i=0;i<scores.size();i++) {
            hs.add(getFormattedScore(i, scores.get(i)));
        }
        return hs.toArray(new String[0]);
    }

    public boolean newHighScore(GameState state) {
        return getLowestScore() < state.getScore();
    }

    private int getLowestScore() {
        return scores.lastElement().score;
    }

    public void addNewScore(Score newScore) {
        scores.add(newScore);
        Collections.sort(scores);
        scores.remove(scores.firstElement());
        Collections.reverse(scores);
        try {
            File file = getHighScoreFile();
            PrintWriter out = new PrintWriter(new FileWriter(file));
            String xml = "<hs>\n";
            for (String[] score : DEFAULT_SCORES) {
                xml += "<player name=\"" + score[0] + "\" score=\"" + score[1] + "\" />\n";
            }
            xml += "</hs>";
            out.write(xml);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
