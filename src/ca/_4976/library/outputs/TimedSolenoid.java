package ca._4976.library.outputs;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.RobotStateListener;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class TimedSolenoid {

    private AsynchronousRobot module;

    private DoubleSolenoid solenoid;
    private long setTime = 0;
    private boolean extended = false;
    private int offDelay = 1000;

    public TimedSolenoid(AsynchronousRobot module, int pcmId, int outPin, int inPin) {

        this.module = module;
        solenoid = new DoubleSolenoid(pcmId, outPin, inPin);

        module.addListener(new RobotStateListener() {

            @Override public void disabledInit() {
               solenoid.set(DoubleSolenoid.Value.kOff);
            }
        });
    }

    public void output(boolean extended) {

        setTime = System.currentTimeMillis();
        this.extended = extended;
        solenoid.set(extended ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
        module.runNextLoop(this::disable, offDelay);
    }

    public boolean isExtended() { return extended; }

    private void disable() {

        if (setTime < System.currentTimeMillis() - (offDelay * 0.90))
            solenoid.set(DoubleSolenoid.Value.kOff);
    }
}
