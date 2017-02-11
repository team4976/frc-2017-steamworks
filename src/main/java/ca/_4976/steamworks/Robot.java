package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.*;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = new XboxController(this, 1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private DriveTrain drive = new DriveTrain(this);
    private MotionControl profile = new MotionControl(this);
    private GearHandling gearHandling = new GearHandling(this);
    public LazySusan lazySusan = new LazySusan(this);
    private shooter_cock Shooter_Cock= new shooter_cock(this);
    private Winch winch = new Winch(this);
    public Elevator elevator = new Elevator(this);

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
        
        System.out.println("Test Initialized!");
    }


}
