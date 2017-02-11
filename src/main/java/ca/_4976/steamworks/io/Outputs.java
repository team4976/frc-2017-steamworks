package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.outputs.*;

public class Outputs {

    //Motors for driving
    public PWMMotor driveLeftFront;
    public PWMMotor driveLeftRear;
    public PWMMotor driveRightFront;
    public PWMMotor driveRightRear;

    public CANMotor HopperElevator;
    public CANMotor ShooterElevator;
    public CANMotor winchLeft;
    public CANMotor winchRight;

    public Solenoid door;
    public Solenoid climb;
    public Solenoid winchArm;

    public LinearActuator hood;
    public CANMotor shooter;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new PWMMotor(module, 1, 0.02);
        driveLeftRear = new PWMMotor(module, 2, 0.02);
        driveRightFront = new PWMMotor(module, 0, 0.02);
        driveRightRear = new PWMMotor(module, 3, 0.02);

        HopperElevator = new CANMotor(2);
        winchLeft = new CANMotor(6);
        winchRight = new CANMotor(7);
        ShooterElevator = new CANMotor(3);
        shooter = new CANMotor(12);

        hood = new LinearActuator(4);

        winchArm = new Solenoid(module, 20, 3, 5);
        door = new Solenoid(module, 20, 1, 6);
        climb = new Solenoid(module, 20, 0, 7);
    }
}
