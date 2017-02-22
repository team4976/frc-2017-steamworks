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
    private Shooter shooter = new Shooter(this);
    private Winch winch = new Winch(this);
    private Agitator agitator = new Agitator(this);
    //public ElevatorBackUp elevatorBackUp = new ElevatorBackUp(this);
    public Elevator elevator = new Elevator(this);
    public LazySusan lazySusan = new LazySusan(this);
}
