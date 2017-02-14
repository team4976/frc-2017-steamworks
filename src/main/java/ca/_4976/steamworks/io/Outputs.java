package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.library.outputs.*;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.VictorSP;

public class Outputs {

    public VictorSP driveLeftFront;
    public VictorSP driveLeftRear;
    public VictorSP driveRightFront;
    public VictorSP driveRightRear;

    public CANTalon hopperElevator;
    public CANTalon shooterElevator;

    public CANTalon winchMaster;

    public CANTalon shooterMaster;

    public Solenoid gearDoor;
    public Solenoid winchArm;

    public LinearActuator shooterHood;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new VictorSP(1);
        driveLeftRear = new VictorSP(2);
        driveRightFront = new VictorSP(0);
        driveRightRear = new VictorSP(3);

        hopperElevator = new CANTalon(2);
        shooterElevator = new CANTalon(3);

        shooterMaster = new CANTalon(11);
        shooterMaster.changeControlMode(CANTalon.TalonControlMode.Speed);
        shooterMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

        CANTalon shooterSlave = new CANTalon(12);
        shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        shooterSlave.set(shooterMaster.getDeviceID());

        winchMaster = new CANTalon(6);

        CANTalon winchSlave = new CANTalon(7);
        winchSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        winchSlave.set(winchMaster.getDeviceID());

        gearDoor = new Solenoid(module, 20, 1, 6);
        winchArm = new Solenoid(module, 20, 0, 7);

        shooterHood = new LinearActuator(4);

        module.addListener(new RobotStateListener() {

            private Compressor compressor = new Compressor(20);

            @Override public void disabledInit() { compressor.setClosedLoopControl(false); }

            @Override public void testInit() { compressor.setClosedLoopControl(true); }
        });
    }
}
