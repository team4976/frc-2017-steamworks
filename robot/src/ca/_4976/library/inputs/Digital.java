package ca._4976.library.inputs;

import ca._4976.library.Initialization;
import ca._4976.library.listeners.BooleanListener;
import edu.wpi.first.wpilibj.DigitalInput;

import java.util.ArrayList;

public class Digital {

    private DigitalInput input;

    private ArrayList<BooleanListener> listeners = new ArrayList<>();
    private boolean[] values = new boolean[2];

    public Digital(int id) {

        input = new DigitalInput(id);

        for (int i = 0; i < values.length; i++) values[i] = input.get();

        Initialization.HARDWARE_INPUT_EVALS.add(() -> {

            values[0] = values[1];
            values[1] = get();

            if (!values[0] && values[1]) for (BooleanListener listener : listeners) listener.rising();

            if (values[0] && !values[1]) for (BooleanListener listener : listeners) listener.falling();

        });
    }

    public boolean get() { return input.get(); }

    public void addListener(BooleanListener listener) { listeners.add(listener); }
}
