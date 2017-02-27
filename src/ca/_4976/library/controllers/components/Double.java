package ca._4976.library.controllers.components;

import ca._4976.library.Evaluable;
import ca._4976.library.Initialization;
import ca._4976.library.listeners.DoubleListener;

public abstract class Double implements Evaluable {

    public enum EVAL_STATE { NON, CHANGED }

    private EVAL_STATE state = EVAL_STATE.NON;

    protected int id;

    private DoubleListener[] listeners = new DoubleListener[0];

    private double[] values = new double[2];

    protected Double(int id) { this.id = id; }

    public int getID() { return id; }

    public abstract double get();

    public void addListener(DoubleListener listener) {

        if (listeners.length == 0) Initialization.USER_INPUT_EVALS.add(this);

        DoubleListener[] temp = listeners.clone();
        listeners = new DoubleListener[temp.length + 1];

        System.arraycopy(temp, 0, listeners, 0, temp.length);

        listeners[listeners.length - 1] = listener;
    }

    public DoubleListener[] getListeners() { return listeners; }

    @Override public void eval() {

        values[0] = values[1];
        values[1] = get();

        if (values[0] != values[1]) {

            for (DoubleListener listener : listeners) listener.changed(values[1]);
            state = EVAL_STATE.CHANGED;

        } else state = EVAL_STATE.NON;
    }

    public EVAL_STATE getState() { return state; }
}
