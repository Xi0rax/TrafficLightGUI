package org.xi0rax.trafficlight.viewmodel;

import org.xi0rax.trafficlight.enums.ControlModes;
import org.xi0rax.trafficlight.enums.OperateModes;
import org.xi0rax.trafficlight.enums.Signals;
import org.xi0rax.trafficlight.interfaces.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class TrafficModel {
    Timer timer = new Timer();

    public TrafficModel() {
        this.commands = new HashMap<String, Command>() {
            {
                put("tladd", new AddTrafficLight());
                put("tlrm", new RemoveTrafficLight());
                put("sigint", new SetSigInterval());
                put("conmode", new SetControlMode());
                put("opmode", new SetOperatingMode());
                put("setsignal", new SetSignal());
                put("getstate", new GetState());
                put("monitor", new GetSignals());
                put("tllist", new TrafficLightList());
            }
        };
    }

    Map<String, Command> commands;
    List<TrafficLight> trafficLights = new ArrayList<>();

    private class AddTrafficLight implements Command {
        @Override
        public String[] execute(String[] args) {
            TrafficLight trafficLight = new TrafficLight();
            trafficLights.add(trafficLight);
            new Thread(trafficLight).start();
            return new String[]{"Traffic light " + (trafficLights.size() - 1) + " added successfully"};
        }
    }

    private class RemoveTrafficLight implements Command {
        @Override
        public String[] execute(String[] args) {
            if (Integer.parseInt(args[0]) >= 0) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                trafficLights.get(trafficLightNumber).setPower(false);
                trafficLights.remove(trafficLightNumber);
                return new String[]{"Traffic light " + trafficLightNumber + " removed successfully"};
            } else {
                return new String[]{"No traffic lights"};
            }
        }
    }

    public class TrafficLightList implements Command {
        @Override
        public String[] execute(String[] args) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < trafficLights.size(); i++) {
                result.add(String.valueOf(i));
            }
            return result.stream().toArray(String[]::new);
        }
    }

    private class SetSigInterval implements Command {
        @Override
        public String[] execute(String[] args) {
            String signal = "";
            int trafficLightNumber = Integer.parseInt(args[0]);
            TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
            switch (args[1]) {
                case "0":
                    signal = "red";
                    trafficLight.setRedInterval(Integer.parseInt(args[2]));
                    break;
                case "1":
                    signal = "yellow";
                    trafficLight.setYellowInterval(Integer.parseInt(args[2]));
                    break;
                case "2":
                    signal = "green";
                    trafficLight.setGreenInterval(Integer.parseInt(args[2]));
                    break;
                case "3":
                    signal = "blink";
                    trafficLight.setYellowBlinkInterval(Integer.parseInt(args[2]));
                    break;
                default:
                    break;
            }
            return new String[]{"Interval of " + signal + " signal has been set to " + args[2] + " sec"};
        }
    }

    private class SetControlMode implements Command {
        @Override
        public String[] execute(String[] args) {
            int trafficLightNumber = Integer.parseInt(args[0]);
            TrafficLight trafficLight = trafficLights.get(trafficLightNumber);

            trafficLight.setControlMode(ControlModes.values()[Integer.parseInt(args[1])]);
            return new String[]{"Control mode of " + trafficLightNumber + " traffic light has been set to " + ControlModes.values()[Integer.parseInt(args[1])].name()};
        }
    }

    private class SetOperatingMode implements Command {
        @Override
        public String[] execute(String[] args) {
            int trafficLightNumber = Integer.parseInt(args[0]);
            TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
            trafficLight.setOperateMode(OperateModes.values()[Integer.parseInt(args[1])]);
            if (trafficLight.getControlMode() == ControlModes.MANUAL) {
                trafficLight.setControlMode(ControlModes.AUTO);
            }
            return new String[]{"Operating mode of " + trafficLightNumber + " traffic light has been set to " + OperateModes.values()[Integer.parseInt(args[1])].name()};
        }
    }

    private class SetSignal implements Command {
        @Override
        public String[] execute(String[] args) {
            String signal = "";
            int trafficLightNumber = Integer.parseInt(args[0]);
            TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
            switch (args[1]) {
                case "0":
                    signal = "red";
                    trafficLight.setSignal(Signals.RED);
                    break;
                case "1":
                    signal = "yellow";
                    trafficLight.setSignal(Signals.YELLOW);
                    break;
                case "2":
                    signal = "green";
                    trafficLight.setSignal(Signals.GREEN);
                    break;
                default:
                    break;
            }
            return new String[]{"Signal of " + trafficLightNumber + " traffic light has been set to " + signal};
        }
    }

    private class GetSignals implements Command {
        @Override
        public String[] execute(String[] args) {
            if (!trafficLights.isEmpty()) {
                TrafficLight trafficLight = trafficLights.get(Integer.parseInt(args[0]));
                int signal = trafficLight.getSignal().ordinal();
                return new String[]{signal == 0 ? "true" : "false", signal == 1 ? "true" : "false", signal == 2 ? "true"
                        : "false", trafficLight.getSignal().name()};
            } else {
                return new String[]{"false", "false", "false", "UNDEFINED"};
            }
        }
    }

    private class GetState implements Command {
        @Override
        public String[] execute(String[] args) {
            int trafficLightNumber = Integer.parseInt(args[0]);
            TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
            int state = trafficLight.getSignal().ordinal();
            return new String[]{
                    String.valueOf(state),
                    String.valueOf(trafficLight.getRedInterval()),
                    String.valueOf(trafficLight.getYellowInterval()),
                    String.valueOf(trafficLight.getGreenInterval()),
                    String.valueOf(trafficLight.getControlMode().ordinal()),
                    String.valueOf(trafficLight.getOperateMode().ordinal()),
                    String.valueOf(trafficLight.getYellowBlinkInterval())
            };
        }
    }

    public String[] parse(String cmd) {
        String[] substrings = cmd.split(" ");
        return commands.get(substrings[0]).execute(Arrays.copyOfRange(substrings, 1, substrings.length));
    }
}
