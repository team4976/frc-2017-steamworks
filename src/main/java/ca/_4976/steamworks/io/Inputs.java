package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.DigitalEncoder;
import ca._4976.library.inputs.Digital;

public class Inputs {

    public DigitalEncoder driveLeft;
    public DigitalEncoder driveRight;

    public Digital optical0, optical1, optical2, TESTINGSWITCH;

    public Digital gearSense;

    public Inputs(AsynchronousRobot module) {

        driveRight = new DigitalEncoder(module, 0, 1);
        driveLeft = new DigitalEncoder(module, 2, 3);

        driveLeft.setReversed(true);
        driveRight.setReversed(true);

        driveLeft.setScalar(0.00031179492);
        driveRight.setScalar(0.00031179492);

        driveLeft.setMinRate(0);
        driveRight.setMinRate(0);

        optical0 = new Digital(module, 4);
        optical1 = new Digital(module, 5);
        optical2 = new Digital(module, 6);
        TESTINGSWITCH = new Digital(module, 9);

        gearSense = new Digital(module, 5);
    }
}
