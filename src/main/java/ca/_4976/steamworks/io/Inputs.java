package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.Digital;
import edu.wpi.first.wpilibj.Encoder;

public class Inputs {

    public Encoder driveLeft;
    public Encoder driveRight;
    public Encoder shooter_encoder;

    public Digital bottomOfHE, bottomOfSHE, topOfSHE, TESTINGSWITCH;
    public Digital winchSensor;
    public Digital gearSense;

    public Inputs(AsynchronousRobot module) {

        driveRight = new Encoder(0, 1);
        driveLeft = new Encoder(2, 3);
        shooter_encoder = new Encoder(12, 13);

        driveLeft.setReverseDirection(true);
        driveRight.setReverseDirection(true);

        driveLeft.setDistancePerPulse(0.00031179492);
        driveRight.setDistancePerPulse(0.00031179492);

        driveLeft.setMinRate(0);
        driveRight.setMinRate(0);

        bottomOfHE = new Digital(module, 4);
        bottomOfSHE = new Digital(module, 5);
        topOfSHE = new Digital(module, 6);
        TESTINGSWITCH = new Digital(module, 9);
        gearSense = new Digital(module, 7);
        winchSensor = new Digital(module, 8);
    }
}
