package frc.team4976.library.inputs;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;
import frc.team4976.library.listeners.BooleanListener;

import java.util.ArrayList;

public class Digital extends Subsystem {

    private DigitalInput input;

    private ArrayList<BooleanListener> listeners = new ArrayList<>();
    private boolean[] values = new boolean[2];
    private int onTime = 0;

    public Digital(IterativeRobotModule module, int id) {

        super(module);
        input = new DigitalInput(id);
    }

    public boolean get() { return input.get(); }

    public void eval() {

        values[0] = values[1];
        values[1] = get();

        if (values[1] && onTime > -1) onTime++; else onTime = 0;

        if (onTime == -1 && !values[1]) onTime = 0;

        if (values[0] != values[1]) for (BooleanListener listener : listeners) listener.changed();

        if (!values[0] && values[1]) for (BooleanListener listener : listeners) listener.rising();

        if (values[0] && !values[1]) for (BooleanListener listener : listeners) listener.falling();

        if (onTime > 24) for (BooleanListener listener : listeners) {

            listener.held();
            onTime = -1;
        }
    }

    public void addListener(BooleanListener listener) { listeners.add(listener); }
}
