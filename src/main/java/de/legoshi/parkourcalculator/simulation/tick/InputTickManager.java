package de.legoshi.parkourcalculator.simulation.tick;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class InputTickManager extends Observable {

    private final List<InputTick> inputTicks = new ArrayList<>();
    @Getter private List<Observer> observers = new ArrayList<>();

    public List<InputTick> getInputTicks() {
        return this.inputTicks;
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }
    
    public void setInputTicks(List<InputTick> inputTicks) {
        this.inputTicks.addAll(inputTicks);
        notifyObservers();
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            if (observer != null) observer.update(this, null);
        }
    }
}
