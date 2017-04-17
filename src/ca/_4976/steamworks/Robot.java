package ca._4976.steamworks;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.XboxController;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;
import ca._4976.steamworks.subsystems.*;
import ca._4976.steamworks.subsystems.profiler.MotionControl;
import ca._4976.steamworks.subsystems.vision.Vision;

public class Robot extends AsynchronousRobot {

    public final Config config = new Config(this);

    public final XboxController driver = new XboxController(0);
    public final XboxController operator =  new XboxController(1);

    public final Inputs inputs = new Inputs(this);
    public final Outputs outputs = new Outputs(this);

    private final GearHandler gearHandler = new GearHandler(this);

    public final Winch winch = new Winch(this);
    public final MotionControl profiler = new MotionControl(this);
    public final Status status = new Status(this);
    public final Shooter shooter = new Shooter(this);
    public final DriveTrain drive = new DriveTrain(this);
    public final Agitator agitator = new Agitator(this);
    public final Elevator elevator = new Elevator(this);
    public final Vision vision = new Vision(this);
}

