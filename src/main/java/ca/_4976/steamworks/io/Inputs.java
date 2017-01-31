package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.DigitalEncoder;

public class Inputs {

    public DigitalEncoder driveLeft;
    public DigitalEncoder driveRight;

    public Inputs(AsynchronousRobot module) {

        driveLeft = new DigitalEncoder(module, 0, 1);
        driveRight = new DigitalEncoder(module, 2, 3);

        driveLeft.setMinRate(0.001);
        driveLeft.setScalar(4.961e-5);
        driveRight.setMinRate(0.001);
        driveRight.setScalar(4.961e-5);
    }
}
