package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.*;
import ca._4976.steamworks.subsystems.profiler.MotionControl;

public class Robot extends AsynchronousRobot {

    public XboxController driver = new XboxController(0);
    public XboxController operator =  new XboxController(1);

    public Inputs inputs = new Inputs(this);
    public Outputs outputs = new Outputs(this);

    private Status status = new Status(this);
    private MotionControl profiler = new MotionControl(this);
    private GearHandler gearHandler = new GearHandler(this);
    private Winch winch = new Winch(this);
    private Shooter shooter = new Shooter(this);

    public DriveTrain drive = new DriveTrain(this);
    public Agitator agitator = new Agitator(this);
    public Elevator elevator = new Elevator(this);
    public VisionTracker vision = new VisionTracker(this);
}
