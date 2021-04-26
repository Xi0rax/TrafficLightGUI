package org.xi0rax.trafficlight.viewmodel;

import javax.swing.*;
import java.awt.*;


public class TrafficLightDrawing extends JComponent {
    private static final int WIDTH = 50;
    private static final int SIZE = 40;
    private static final int HEIGHT = 130;
    private static final int MARGIN_X = 10;
    private static final int MARGIN_Y = 10;

    public boolean[] getState() {
        return state;
    }

    public void setState(boolean[] state) {
        this.state = state;
    }

    private boolean[] state;

    {
        state = new boolean[]{false, false, false};
    }


    @Override
    public void paint(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);
        if (state[0]) {
            g.fillOval(MARGIN_X, MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        } else {
            g.drawOval(MARGIN_X, MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        }

        g.setColor(Color.YELLOW);
        if (state[1]) {
            g.fillOval(MARGIN_X, SIZE + MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        } else {
            g.drawOval(MARGIN_X, SIZE + MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        }

        g.setColor(Color.GREEN);
        if (state[2]) {
            g.fillOval(MARGIN_X, 2 * SIZE + MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        } else {
            g.drawOval(MARGIN_X, 2 * SIZE + MARGIN_Y, SIZE - MARGIN_X, SIZE - MARGIN_Y);
        }
    }
}
