package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.outputs.PWMMotor;
import ca._4976.library.outputs.Solenoid;

public class Outputs {

    public PWMMotor driveLeftFront;
    public PWMMotor driveLeftRear;
    public PWMMotor driveRightFront;
    public PWMMotor driveRightRear;

    public Solenoid door;
    public Solenoid climb;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new PWMMotor(module, 1, 0.02);
        driveLeftRear = new PWMMotor(module, 2, 0.02);
        driveRightFront = new PWMMotor(module, 0, 0.02);
        driveRightRear = new PWMMotor(module, 3, 0.02);


        door = new Solenoid(module, 20, 1, 2);
        climb = new Solenoid(module, 20, 3, 4);
    }
}
