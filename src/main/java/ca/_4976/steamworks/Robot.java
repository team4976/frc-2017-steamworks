package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.DriveTrain;
import ca._4976.steamworks.subsystems.MotionControl;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = new XboxController(this, 1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionControl profile = new MotionControl(this);

    @Override public void robotInit() {

        System.out.println("Robot Initalized!");
    }

    @Override public void disabledInit() { 
        
        System.out.println("Robot was Disabled!");
    }

    @Override public void autonomousInit() {
        
        profile.play();
    
        System.out.println("Autonomous Initalized!");
    }

    @Override public void teleopInit() {

        System.out.println("Operator Control Initalized!");
    }

    @Override public void testInit() {
        
        if (operator.BACK.get() || driver.BACK.get()) {

            enableOperatorControl();
            profile.record();
        }
    
        System.out.println("Autonomous Initalized!");
    }
}
