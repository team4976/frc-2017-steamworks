package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.inputs.Digital;
import ca._4976.library.inputs.DigitalEncoder;
import edu.wpi.first.wpilibj.DigitalInput;

public class Inputs {

    public DigitalEncoder driveLeft;
    public DigitalEncoder driveRight;

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

        gearSense = new Digital(module, 5);
    }
}
