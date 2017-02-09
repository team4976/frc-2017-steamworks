package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.outputs.CANMotor;
import ca._4976.library.outputs.LinearActuator;
import ca._4976.library.outputs.PWMMotor;
import ca._4976.library.outputs.Solenoid;
import ca._4976.steamworks.subsystems.shooter_cock;

public class Outputs {

    public PWMMotor driveLeftFront;
    public PWMMotor driveLeftRear;
    public PWMMotor driveRightFront;
    public PWMMotor driveRightRear;
    public LinearActuator hood;
    public CANMotor shooter;
    public Solenoid solenoid;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new PWMMotor(module, 1, 0.02);
        driveLeftRear = new PWMMotor(module, 2, 0.02);
        driveRightFront = new PWMMotor(module, 0, 0.02);
        driveRightRear = new PWMMotor(module, 3, 0.02);
        shooter = new CANMotor(12);

        solenoid = new Solenoid(module, 20, 1, 2);
        hood = new LinearActuator(4);
    }
}
