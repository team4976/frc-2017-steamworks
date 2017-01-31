package ca._4976.library.inputs;

import ca._4976.library.AsynchronousRobot;
import edu.wpi.first.wpilibj.DigitalInput;
import ca._4976.library.listeners.BooleanListener;

import java.util.ArrayList;

public class Digital {

    private AsynchronousRobot module;

    private DigitalInput input;

    private ArrayList<BooleanListener> listeners = new ArrayList<>();
    private boolean[] values = new boolean[2];

    public Digital(AsynchronousRobot module, int id) {

        this.module = module;
        input = new DigitalInput(id);
    }

    public boolean get() { return input.get(); }

    public void eval() {

        values[0] = values[1];
        values[1] = get();

        if (values[0] != values[1]) for (BooleanListener listener : listeners) listener.changed();

        if (!values[0] && values[1]) for (BooleanListener listener : listeners) listener.rising();

        if (values[0] && !values[1]) for (BooleanListener listener : listeners) listener.falling();
    }

    public void addListener(BooleanListener listener) { listeners.add(listener); }
}
