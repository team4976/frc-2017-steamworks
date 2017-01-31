package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.motionprofiler.MotionProfile;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = driver;

    public NetworkTable table = NetworkTable.getTable("encoder");

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);

    @Override public void disabledInit() {

        outputs.compressor.setClosedLoopControl(false);
    }

    @Override public void teleopInit() {

        System.out.println("hello");
        setCheckEncoder();
        runNextLoop(this::setCheckEncoder, -1);
    }

    public void setCheckEncoder() { table.putNumber("value", inputs.driveRight.getDistance()); }

    @Override public void autonomousInit() { profile.run(); }

    @Override public void testInit() {

        outputs.compressor.setClosedLoopControl(true);

        if (operator.BACK.get() || true) {

            enableOperatorControl();
            profile.record();
        }
    }
}
