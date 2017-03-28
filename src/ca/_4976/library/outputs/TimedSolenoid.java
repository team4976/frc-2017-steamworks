package ca._4976.library.outputs;

import ca._4976.library.AsynchronousRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class TimedSolenoid {

    private AsynchronousRobot module;

    private DoubleSolenoid solenoid;
    private long setTime = 0;
    private boolean extened = false;
    private int offDelay = 1000;

    public TimedSolenoid(AsynchronousRobot module, int pcmId, int outPin, int inPin) {

        this.module = module;
        solenoid = new DoubleSolenoid(pcmId, outPin, inPin);
    }

    public void output(boolean extended) {

        setTime = System.currentTimeMillis();
        this.extened = extended;
        solenoid.set(extended ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
        module.runNextLoop(this::disable, offDelay);
    }

    public boolean isExtened() { return extened; }

    private void disable() {

        if (setTime < System.currentTimeMillis() - (offDelay * 0.90))
            solenoid.set(DoubleSolenoid.Value.kOff);
    }
}
