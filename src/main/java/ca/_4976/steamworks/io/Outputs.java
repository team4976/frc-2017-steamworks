package ca._4976.steamworks.io;

import ca._4976.library.IterativeRobot;
import ca._4976.library.outputs.CANMotor;
import ca._4976.library.outputs.Solenoid;

public class Outputs {

    private IterativeRobot module;

    public Outputs(IterativeRobot module) { this.module = module; }

    public CANMotor driveLeftFront = new CANMotor(module, 11, 0.02);
    public CANMotor driveLeftRear = new CANMotor(module, 12, 0.02);
    public CANMotor driveRightFront = new CANMotor(module, 13, 0.02);
    public CANMotor driveRightRear = new CANMotor(module, 14, 0.02);

    public Solenoid shifter = new Solenoid(module, 20, 0, 1);
}
