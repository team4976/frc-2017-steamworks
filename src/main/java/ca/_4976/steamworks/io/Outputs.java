package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.outputs.CANMotor;
import ca._4976.library.outputs.PWMMotor;
import ca._4976.library.outputs.Solenoid;

public class Outputs {

    //Motors for driving
    public PWMMotor driveLeftFront;
    public PWMMotor driveLeftRear;
    public PWMMotor driveRightFront;
    public PWMMotor driveRightRear;

    //Elevator motors
    public CANMotor HopperElevator;
    public CANMotor ShooterElevator;

    public Solenoid solenoid;

    public Outputs(AsynchronousRobot module) {

        //Setting drive motors
        driveLeftFront = new PWMMotor(module, 1, 0.02);
        driveLeftRear = new PWMMotor(module, 2, 0.02);
        driveRightFront = new PWMMotor(module, 0, 0.02);
        driveRightRear = new PWMMotor(module, 3, 0.02);

        //Setting elevator motors
        //Ports to be determined
        HopperElevator = new CANMotor(2);
        ShooterElevator = new CANMotor(3);

        //Setting solenoid
        solenoid = new Solenoid(module, 20, 1, 2);
    }
}
