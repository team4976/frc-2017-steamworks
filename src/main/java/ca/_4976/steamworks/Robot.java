package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.Elevator;
import ca._4976.steamworks.subsystems.motionprofiler.MotionProfile;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = driver;

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionProfile profile = new MotionProfile(this);
    private Elevator elevator = new Elevator(this);

    public void getEncoderDistance() {

        if (isOperatorControl()) {

            //System.out.print("velocity: " + inputs.driveLeft.getRate());
            //System.out.println(" velocity: " + inputs.driveRight.getRate());
        }
    }

    @Override public void robotInit() {

        runNextLoop(this::getEncoderDistance, -1);
        inputs.TESTINGSWITCH.addListener(new BooleanListener() {
            @Override
            public void rising() {
                 System.out.println("switch was flipped");
            }
        });
//        driver.A.addListener(new ButtonListener() {
//            @Override
//            public void pressed() {
//                elevator.runSHE(0.5);
//                elevator.runHE(0.5);
//             //   outputs.HopperElevator.set(0.5);
//             //   outputs.ShooterElevator.set(0.5);
//                System.out.println("running motors");
//            }
//        });
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
