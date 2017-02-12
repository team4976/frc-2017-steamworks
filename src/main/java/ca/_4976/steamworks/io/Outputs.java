package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.outputs.*;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.VictorSP;

public class Outputs {

    //Motors for driving
    public VictorSP driveLeftFront;
    public VictorSP driveLeftRear;
    public VictorSP driveRightFront;
    public VictorSP driveRightRear;

    public CANTalon HopperElevator;
    public CANTalon ShooterElevator;
    public CANTalon winchLeft;
    public CANTalon winchRight;

    public Solenoid door;
    public Solenoid climb;
    public Solenoid winchArm;

    public LinearActuator hood;
    public CANTalon shooter;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new VictorSP(1);
        driveLeftRear = new VictorSP(2);
        driveRightFront = new VictorSP(0);
        driveRightRear = new VictorSP(3);

        HopperElevator = new CANTalon(2);
        winchLeft = new CANTalon(6);
        winchRight = new CANTalon(7);
        ShooterElevator = new CANTalon(3);
        shooter = new CANTalon(12);

        hood = new LinearActuator(4);

        winchArm = new Solenoid(module, 20, 3, 5);
        door = new Solenoid(module, 20, 1, 6);
        climb = new Solenoid(module, 20, 0, 7);
    }
}
