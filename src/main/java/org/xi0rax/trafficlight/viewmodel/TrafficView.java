package org.xi0rax.trafficlight.viewmodel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public class TrafficView extends JDialog {
    Timer timer = new Timer();
    TimerTask task = null;

    private JPanel contentPanel;
    private JButton buttonExit;
    private JRadioButton normalModeRadioButton;
    private JRadioButton nightModeRadioButton;
    private JTrafficLight trafficLight;
    private JRadioButton automaticModeRadioButton;
    private JRadioButton manualModeRadioButton;
    private JPanel controlPanel;
    private JSpinner redIntervalSpinner;
    private JSpinner yellowIntervalSpinner;
    private JSpinner greenIntervalSpinner;
    private JRadioButton setRedRadioButton;
    private JRadioButton setYellowRadioButton;
    private JRadioButton setGreenRadioButton;
    private JComboBox<String> trafficLightSelector;
    private JButton addButton;
    private JButton removeButton;
    private JTextField currentSignal;
    private JSpinner blinkIntervalSpinner;
    private JPanel nightPanel;
    private JTextField statusField;
    private JPanel operateModePanel;
    private JPanel automaticPanel;
    private JPanel manualPanel;
    private final ButtonGroup operateModeGroup = new ButtonGroup();
    private final ButtonGroup signalGroup = new ButtonGroup();
    private final ButtonGroup controlModeGroup = new ButtonGroup();
    private final TrafficViewModel trafficViewModel = new TrafficViewModel();

    public TrafficView() {
        operateModeGroup.add(normalModeRadioButton);
        operateModeGroup.add(nightModeRadioButton);

        controlModeGroup.add(automaticModeRadioButton);
        controlModeGroup.add(manualModeRadioButton);

        signalGroup.add(setRedRadioButton);
        signalGroup.add(setYellowRadioButton);
        signalGroup.add(setGreenRadioButton);

        setContentPane(contentPanel);
        setModal(true);
        setResizable(false);
        updateUI();

        setRedRadioButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.setSignal(trafficLightSelector.getSelectedIndex(), 0)[0]);
            updateUI();
        });

        setYellowRadioButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.setSignal(trafficLightSelector.getSelectedIndex(), 1)[0]);
            updateUI();
        });

        setGreenRadioButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.setSignal(trafficLightSelector.getSelectedIndex(), 2)[0]);
            updateUI();
        });

        redIntervalSpinner.addChangeListener(e -> {
            statusField.setText(trafficViewModel.setInterval(trafficLightSelector.getSelectedIndex()
                    , 0, (int) redIntervalSpinner.getValue())[0]);
            updateUI();
        });

        yellowIntervalSpinner.addChangeListener(e -> {
            statusField.setText(trafficViewModel.setInterval(trafficLightSelector.getSelectedIndex()
                    , 1, (int) yellowIntervalSpinner.getValue())[0]);
            updateUI();
        });

        greenIntervalSpinner.addChangeListener(e -> {
            statusField.setText(trafficViewModel.setInterval(trafficLightSelector.getSelectedIndex()
                    , 2, (int) greenIntervalSpinner.getValue())[0]);
            updateUI();
        });

        blinkIntervalSpinner.addChangeListener(e -> {
            statusField.setText(trafficViewModel.setInterval(trafficLightSelector.getSelectedIndex()
                    , 3, (int) blinkIntervalSpinner.getValue())[0]);
            updateUI();
        });

        addButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.addTrafficLight()[0]);
            updateTrafficLights(trafficViewModel.getTrafficLights());
            updateUI();
        });
        removeButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.removeTrafficLight(trafficLightSelector.getSelectedIndex())[0]);
            updateTrafficLights(trafficViewModel.getTrafficLights());
            updateUI();
        });
        normalModeRadioButton.addActionListener(e -> {
                    statusField.setText(trafficViewModel.setOperateMode(trafficLightSelector.getSelectedIndex(), 0)[0]);
                    updateUI();
                }
        );
        nightModeRadioButton.addActionListener(e -> {
                    statusField.setText(trafficViewModel.setOperateMode(trafficLightSelector.getSelectedIndex(), 1)[0]);
                    updateUI();
                }
        );

        automaticModeRadioButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.setControlMode(trafficLightSelector.getSelectedIndex(), 0)[0]);
            updateUI();
        });

        manualModeRadioButton.addActionListener(e -> {
            statusField.setText(trafficViewModel.setControlMode(trafficLightSelector.getSelectedIndex(), 1)[0]);
            updateUI();
        });

        trafficLightSelector.addActionListener(e -> {
            monitorTrafficLight();
            updateUI();
        });


        buttonExit.addActionListener(e -> onExit());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        contentPanel.registerKeyboardAction(e -> onExit(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onExit() {
        dispose();
    }

    private void updateTrafficLights(String[] items) {
        ComboBoxModel<String> model = new DefaultComboBoxModel<>(items);
        this.trafficLightSelector.setModel(model);
        if (items.length > 0) {
            this.trafficLightSelector.setSelectedIndex(model.getSize() - 1);
            this.operateModePanel.setEnabled(true);
            Component[] components = this.operateModePanel.getComponents();
            for (Component component : components) {
                component.setEnabled(true);
            }
        }
    }

    private void disableAllElements() {
        setComponentEnabled(this.operateModePanel, false);
        setComponentEnabled(this.controlPanel, false);
        setComponentEnabled(this.nightPanel, false);
        setComponentEnabled(this.automaticPanel, false);
        setComponentEnabled(this.manualPanel, false);
        terminateMonitoring();
    }

    private void updateUI() {
        if (this.trafficViewModel.getTrafficLights().length == 0) {
            disableAllElements();
            return;
        } else {
            setComponentEnabled(this.operateModePanel, true);
        }
        String[] result = this.getState(trafficLightSelector.getSelectedIndex());
        int controlMode = Integer.parseInt(result[4]);
        int operateMode = Integer.parseInt(result[5]);
        int redInterval = Integer.parseInt(result[1]);
        int yellowInterval = Integer.parseInt(result[2]);
        int greenInterval = Integer.parseInt(result[3]);
        int blinkInterval = Integer.parseInt(result[6]);

        if (operateMode == 0) {
            operateModeGroup.setSelected(normalModeRadioButton.getModel(), true);
            setComponentEnabled(this.controlPanel, true);
            setComponentEnabled(this.nightPanel, false);
        } else {
            operateModeGroup.setSelected(nightModeRadioButton.getModel(), true);
            setComponentEnabled(this.controlPanel, false);
            setComponentEnabled(this.nightPanel, true);
        }

        if (controlMode == 0) {
            controlModeGroup.setSelected(this.automaticModeRadioButton.getModel(), true);
            setComponentEnabled(this.automaticPanel, operateMode == 0);
            setComponentEnabled(this.manualPanel, false);
        } else {
            controlModeGroup.setSelected(this.manualModeRadioButton.getModel(), true);
            setComponentEnabled(this.automaticPanel, false);
            setComponentEnabled(this.manualPanel, true);
        }

        redIntervalSpinner.setValue(redInterval);
        yellowIntervalSpinner.setValue(yellowInterval);
        greenIntervalSpinner.setValue(greenInterval);
        blinkIntervalSpinner.setValue(blinkInterval);
    }

    private String[] getState(int index) {
        return this.trafficViewModel.getState(index);
    }

    private void setComponentEnabled(JComponent element, boolean state) {
        element.setEnabled(state);
        Component[] components = element.getComponents();
        for (Component component : components) {
            component.setEnabled(state);
        }
    }

    private void terminateMonitoring() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        trafficLight.setState(new boolean[]{false, false, false});
        currentSignal.setText("UNDEFINED");
    }


    private void monitorTrafficLight() {
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    int index = trafficLightSelector.getSelectedIndex();
                    if (index >= 0) {
                        String[] signals = trafficViewModel.getSignals(index);
                        boolean[] state = new boolean[]{Boolean.parseBoolean(signals[0]), Boolean.parseBoolean(signals[1]),
                                Boolean.parseBoolean(signals[2])};
                        trafficLight.setState(state);
                        currentSignal.setText(signals[3]);
                        signalGroup.setSelected(setRedRadioButton.getModel(), state[0]);
                        signalGroup.setSelected(setYellowRadioButton.getModel(), state[1]);
                        signalGroup.setSelected(setGreenRadioButton.getModel(), state[2]);
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 50);
        }
    }

    public static void main(String[] args) {
        TrafficView dialog = new TrafficView();
        dialog.pack();
        dialog.setTitle("Traffic Control System");
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        trafficLight = new JTrafficLight();
        SpinnerModel redIntervalModel = new SpinnerNumberModel(1, 1, 15, 1);
        SpinnerModel yellowIntervalModel = new SpinnerNumberModel(1, 1, 15, 1);
        SpinnerModel greenIntervalModel = new SpinnerNumberModel(1, 1, 15, 1);
        SpinnerModel blinkIntervalModel = new SpinnerNumberModel(1, 1, 15, 1);

        redIntervalSpinner = new JSpinner(redIntervalModel);
        yellowIntervalSpinner = new JSpinner(yellowIntervalModel);
        greenIntervalSpinner = new JSpinner(greenIntervalModel);
        blinkIntervalSpinner = new JSpinner(blinkIntervalModel);
    }
}
