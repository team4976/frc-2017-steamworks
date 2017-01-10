package frc.team4976.steamworks;

import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.controllers.XboxController;
import frc.team4976.steamworks.io.Inputs;
import frc.team4976.steamworks.io.Outputs;
import frc.team4976.steamworks.subsystems.DriveTrain;

public class RobotModule extends IterativeRobotModule {

    public XboxController driver = new XboxController(0);
    public XboxController operator = new XboxController(1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);

    @Override public String getModuleName() {
        return "Project_SteamWorks";
    }

    @Override public String getModuleVersion() {
        return "0.0.1";
    }

    @Override public void robotInit() {

        drive.init();
    }

    @Override public void teleopPeriodic() {

        super.teleopPeriodic();

        driver.eval();
    }
}
