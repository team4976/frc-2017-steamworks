package frc.team4976.library.outputs;

import edu.wpi.first.wpilibj.DoubleSolenoid;

import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;

public class Solenoid extends Subsystem {

    private DoubleSolenoid solenoid;
    private long setTime = 0;
    private int offDelay = 600;

    public Solenoid(IterativeRobotModule module, int pcmId, int outPin, int inPin) {

        super(module);
        solenoid = new DoubleSolenoid(pcmId, outPin, inPin);
    }

    public void output(boolean extended) {

        setTime = System.currentTimeMillis();
        solenoid.set(extended ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
        module.runNextLoop(this::disable, offDelay);
    }

    private void disable() {

        if (setTime < System.currentTimeMillis() - (offDelay * 0.90))
            solenoid.set(DoubleSolenoid.Value.kOff);
    }
}
