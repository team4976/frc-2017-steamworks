package ca._4976.steamworks.io;

import ca._4976.library.IterativeRobot;
import ca._4976.library.outputs.CANMotor;
import ca._4976.library.outputs.PWMMotor;
import ca._4976.library.outputs.Solenoid;

public class Outputs {

    private IterativeRobot module;

    public Outputs(IterativeRobot module) { this.module = module; }

    public PWMMotor driveLeftFront = new PWMMotor(module, 0, 0.02);
    public PWMMotor driveLeftRear = new PWMMotor(module, 1, 0.02);
    public PWMMotor driveRightFront = new PWMMotor(module, 2, 0.02);
    public PWMMotor driveRightRear = new PWMMotor(module, 3, 0.02);

    public Solenoid shifter = new Solenoid(module, 20, 0, 1);
}
