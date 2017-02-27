package ca._4976.library.controllers.components;

import ca._4976.library.Evaluable;
import ca._4976.library.Initialization;
import ca._4976.library.listeners.ButtonListener;

public abstract class Boolean implements Evaluable {

    public enum EVAL_STATE { NON, FALLING, RISING, PRESSED, HELD }

    private EVAL_STATE state = EVAL_STATE.NON;

    protected int id;

    private ButtonListener[] listeners = new ButtonListener[0];

    private boolean[] values = new boolean[2];

    private int onTime = 0;

    protected Boolean(int id) { this.id = id; }

    public int getID() { return id; }

    public abstract boolean get();

    public void addListener(ButtonListener listener) {

        if (listeners.length == 0) Initialization.USER_INPUT_EVALS.add(this);

        ButtonListener[] temp = listeners.clone();
        listeners = new ButtonListener[temp.length + 1];

        System.arraycopy(temp, 0, listeners, 0, temp.length);

        listeners[listeners.length - 1] = listener;
    }

    public ButtonListener[] getListeners() { return listeners; }

    @Override public void eval() {

        values[0] = values[1];
        values[1] = get();
        state = EVAL_STATE.NON;

        if (!values[0] && values[1]) {

            for (ButtonListener listener : listeners) listener.rising();
            state = EVAL_STATE.RISING;

        } else if (values[0] && !values[1]) {

            for (ButtonListener listener : listeners) listener.falling();

            if (onTime < 15) {

                for (ButtonListener listener : listeners) listener.pressed();
                state = EVAL_STATE.PRESSED;

            } else state = EVAL_STATE.FALLING;
        }

        if (onTime == 15) for (ButtonListener listener : listeners) listener.held();

        onTime = values[1] ? onTime + 1 : 0;
    }

    public EVAL_STATE getState() { return state; }
}
