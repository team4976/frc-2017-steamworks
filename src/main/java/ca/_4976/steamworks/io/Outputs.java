package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.outputs.PWMMotor;
import ca._4976.library.outputs.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

public class Outputs {

    public PWMMotor driveLeftFront;
    public PWMMotor driveLeftRear;
    public PWMMotor driveRightFront;
    public PWMMotor driveRightRear;

    public Solenoid Orifice;

    public Compressor compressor;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new PWMMotor(module, 0, 0.03);
        driveLeftRear = new PWMMotor(module, 2, 0.03);
        driveRightFront = new PWMMotor(module, 1, 0.03);
        driveRightRear = new PWMMotor(module, 3, 0.03);

        Orifice = new Solenoid(module, 20, 0, 1);

        compressor = new Compressor(20);
    }
}
