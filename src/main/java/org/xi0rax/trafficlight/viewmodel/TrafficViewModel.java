package org.xi0rax.trafficlight.viewmodel;

public class TrafficViewModel {
    private final TrafficModel trafficModel;

    public TrafficViewModel() {
        this.trafficModel = new TrafficModel();
    }

    public String[] addTrafficLight() {
        return this.trafficModel.parse("tladd");
    }

    public String[] removeTrafficLight(int index) {
        return this.trafficModel.parse(String.format("tlrm %d", index));
    }

    public String[] getTrafficLights() {
        return this.trafficModel.parse("tllist");
    }

    public String[] setControlMode(int index, int mode) {
        return this.trafficModel.parse(String.format("conmode %d %d", index, mode));
    }

    public String[] setOperateMode(int index, int mode) {
        return this.trafficModel.parse(String.format("opmode %d %d", index, mode));
    }

    public String[] setInterval(int index, int color, int delay) {
        return this.trafficModel.parse(String.format("sigint %d %d %d", index, color, delay));
    }

    public String[] setSignal(int index, int signal) {
        return this.trafficModel.parse(String.format("setsignal %d %d", index, signal));
    }

    public String[] getSignals(int index) {
        return this.trafficModel.parse(String.format("monitor %d", index));
    }

    public String[] getState(int index) {
        return this.trafficModel.parse("getstate " + index);
    }
}
