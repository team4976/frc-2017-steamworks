package frc.team4976.steamworks;

import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.controllers.XboxController;
import frc.team4976.steamworks.io.Inputs;
import frc.team4976.steamworks.io.Outputs;
import frc.team4976.steamworks.subsystems.DriveTrain;
import frc.team4976.steamworks.subsystems.motionprofiler.MotionProfile;

public class RobotModule extends IterativeRobotModule {

    public XboxController driver = new XboxController(0);
    public XboxController operator = new XboxController(1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);

    private boolean isRecording = false;

    @Override public String getModuleName() {
        return "Project_SteamWorks";
    }

    @Override public String getModuleVersion() {
        return "0.0.1";
    }

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

        if (operator.BACK.get() || true) {

            isRecording = true;
            profile.record();
        }
    }

    @Override public void testPeriodic() { if (isRecording) teleopPeriodic(); }

}
