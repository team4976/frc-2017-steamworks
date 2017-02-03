package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.GearHandling;
import ca._4976.steamworks.subsystems.motionprofiler.MotionProfile;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = driver;

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);
    private GearHandling gearHandling = new GearHandling(this);

    public void getEncoderDistance() {

        if (isOperatorControl()) {

            System.out.print("velocity: " + inputs.driveLeft.getRate());
            System.out.println(" velocity: " + inputs.driveRight.getRate());
        }
    }

    @Override public void robotInit() {

        runNextLoop(this::getEncoderDistance, -1);
    }

    @Override public void autonomousInit() { profile.run(); }

    @Override public void teleopInit() {

        System.out.println("Operator Control Enabled");
    }

    @Override public void testInit() {

        System.out.println("Test Initialized");

        if (operator.BACK.get() || driver.BACK.get() || true) {

            enableOperatorControl();
            profile.record();
        }
    }


}
