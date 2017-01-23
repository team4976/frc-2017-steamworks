package ca._4976.steamworks;

import ca._4976.library.IterativeRobotModule;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.motionprofiler.MotionProfile;

public class Robot extends IterativeRobotModule {

    public XboxController driver = new XboxController(0);
    public XboxController operator = new XboxController(1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);

    private boolean isRecording = false;

    @Override public void robotInit() {

        drive.init();
    }

    @Override public void disabledInit() { }

    @Override public void autonomousInit() { profile.run(); }

    @Override public void teleopPeriodic() {

        super.teleopPeriodic();

        driver.eval();
    }

    @Override public void testInit() {

        if (operator.BACK.get()) {

            isRecording = true;
            profile.record();
        }
    }

    @Override public void testPeriodic() { if (isRecording) teleopPeriodic(); }

}
