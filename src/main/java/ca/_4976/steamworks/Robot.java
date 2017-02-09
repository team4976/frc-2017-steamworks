package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.GearHandling;
import ca._4976.steamworks.subsystems.MotionControl;
import ca._4976.steamworks.subsystems.LazySusan;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = new XboxController(this, 1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionControl profile = new MotionControl(this);
    private GearHandling gearHandling = new GearHandling(this);
    private LazySusan lazySusan = new LazySusan(this);

    @Override public void robotInit() {
    
        System.out.println("Robot Initalized!");
    }

    @Override public void disabledInit() {  
    
        System.out.println("Robot was Disabled!");
    }

    @Override public void autonomousInit() { 
    
        System.out.println("Autonomous Initialized!");
    }

    @Override public void teleopInit() { 
    
        System.out.println("Operator Control Initialized!");
    }

    @Override public void testInit() {

        if (operator.BACK.get() || driver.BACK.get()) {

            enableOperatorControl();
            profile.record();
        }

        System.out.println("Test Initalized!");
    }
}
