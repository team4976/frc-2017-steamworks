package frc.team4976.steamworks.io;

import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;
import frc.team4976.library.outputs.CANMotor;

public class Outputs extends Subsystem {

    public Outputs(IterativeRobotModule module) { super(module); }

    public CANMotor driveLeftFront = new CANMotor(module, 11, 0.02);
    public CANMotor driveLeftRear = new CANMotor(module, 12, 0.02);
    public CANMotor driveRightFront = new CANMotor(module, 13, 0.02);
    public CANMotor driveRightRear = new CANMotor(module, 14, 0.02);
}
