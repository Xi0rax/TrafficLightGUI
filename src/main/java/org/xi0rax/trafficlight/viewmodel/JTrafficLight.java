package org.xi0rax.trafficlight.viewmodel;

import javax.swing.*;
import java.awt.*;

public class JTrafficLight extends JPanel {
    TrafficLightDrawing drawing = new TrafficLightDrawing();
    private boolean[] signals = new boolean[]{false, false, false};

    public void setState(boolean[] state) {
        drawing.setState(state);
        drawing.repaint();
    }

    public boolean[] getState() {
        return drawing.getState();
    }

    JTrafficLight() {
        drawing.setPreferredSize(new Dimension(60, 140));
        add(drawing);
    }
}
