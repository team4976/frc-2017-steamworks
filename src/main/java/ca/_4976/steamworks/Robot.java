package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(this, 0);
    public XboxController operator = new XboxController(this, 1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    @Override public void robotInit() {
    
        System.out.println("Robot Initalized!")
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
