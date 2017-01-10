package frc.team4976.library.controllers;

import frc.team4976.library.listeners.DoubleListener;

import java.util.ArrayList;

public class Axis {

    protected int a = -1;
    protected int b = -1;

    private ArrayList<DoubleListener> listeners = new ArrayList<>();
    private double[] values = new double[2];

    public Axis(int a) { this.a = a; }

    public Axis(int a, int b) {

        this.a = a;
        this.b = b;
    }

    protected double get() { throw new NullPointerException(); }

    public void eval() {

        values[0] = values[1];
        values[1] = get();

        if (values[0] != values[1]) for (DoubleListener listener : listeners) listener.changed(values[1]);
    }

    public void addListener(DoubleListener listener) { listeners.add(listener); }
}