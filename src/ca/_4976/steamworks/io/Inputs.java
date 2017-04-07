package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.Digital;
import edu.wpi.first.wpilibj.Encoder;

public class Inputs {

    public Encoder driveLeft;
    public Encoder driveRight;

    public Inputs(AsynchronousRobot module) {

        driveRight = new Encoder(0, 1);
        driveLeft = new Encoder(2, 3);

        driveLeft.setReverseDirection(true);
        driveRight.setReverseDirection(true);

        driveLeft.setDistancePerPulse(0.0001114); //THE POWER OF 1114
        driveRight.setDistancePerPulse(0.0001114);

        driveLeft.setMinRate(0);
        driveRight.setMinRate(0);
    }
}
