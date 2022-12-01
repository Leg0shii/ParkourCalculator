package de.legoshi.parkourcalculator.parkour.tick;

import de.legoshi.parkourcalculator.Controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class InputTickManager extends Observable implements Observer {

    private final ArrayList<InputTick> inputTicks = new ArrayList<>();
    private Controller controller;

    public ArrayList<InputTick> getInputTicks() {
        return this.inputTicks;
    }

    public void addObserver(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void update(Observable o, Object arg) {
        controller.update(o, arg);
    }
}
