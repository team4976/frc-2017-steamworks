package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.Digital;
import ca._4976.library.inputs.DigitalEncoder;
import edu.wpi.first.wpilibj.DigitalInput;

public class Inputs {

    public DigitalEncoder driveLeft;
    public DigitalEncoder driveRight;
    public Digital optical0, optical1, optical2, TESTINGSWITCH;

    public Inputs(AsynchronousRobot module) {

        driveRight = new DigitalEncoder(module, 0, 1);
        driveLeft = new DigitalEncoder(module, 2, 3);

        driveLeft.setReversed(true);
        driveRight.setReversed(true);

        driveLeft.setScalar(0.00031179492);
        driveRight.setScalar(0.00031179492);

        driveLeft.setMinRate(0);
        driveRight.setMinRate(0);

        //Optical0 is the bottom of the hopper elevator
        //Optical1 is the bottom of the shooter elevator/end of hopper elevator
        //Optical2 is the top of shooter elevator

        optical0 = new Digital(module, 4);
        optical1 = new Digital(module, 5);
        optical2 = new Digital(module, 6);
        TESTINGSWITCH = new Digital(module, 9);
    }
}
