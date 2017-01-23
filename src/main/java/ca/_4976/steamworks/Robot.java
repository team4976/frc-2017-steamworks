package ca._4976.steamworks;

import ca._4976.library.IterativeRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.motionprofiler.MotionProfile;

public class Robot extends IterativeRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = new XboxController(this, 1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);

    @Override public void robotInit() {

        drive.init();
    }

    @Override public void autonomousInit() { profile.run(); }

    @Override public void testInit() {

        if (operator.BACK.get()) enableOperatorControl();
    }
}
