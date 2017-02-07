package ca._4976.library.controllers;

import ca._4976.library.listeners.ButtonListener;

import java.util.ArrayList;

public class Button {

    int id;

    private ArrayList<ButtonListener> listeners = new ArrayList<>();
    private boolean[] values = new boolean[2];
    private int onTime = 0;

    Button(int id) { this.id = id; }

    public boolean get() { throw new NullPointerException(); }

    void eval() {

        values[0] = values[1];
        values[1] = get();

        if (!values[0] && values[1]) listeners.forEach(ButtonListener::rising);

        else if (values[0] && !values[1]) listeners.forEach(ButtonListener::falling);

        if (values[0] && !values[1] && onTime < 25) listeners.forEach(ButtonListener::pressed);

        else if (values[0] && !values[1]) listeners.forEach(ButtonListener::pressed);

        if (values[1]) onTime++;

        else onTime = 0;
    }

    public void addListener(ButtonListener listener) { listeners.add(listener); }
}