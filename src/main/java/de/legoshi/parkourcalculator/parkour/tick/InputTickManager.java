package de.legoshi.parkourcalculator.parkour.tick;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class InputTickManager extends Observable {

    private final ArrayList<InputTick> inputTicks = new ArrayList<>();
    private ArrayList<Observer> observers = new ArrayList<>();

    public ArrayList<InputTick> getInputTicks() {
        return this.inputTicks;
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this, null);
        }
    }
}
