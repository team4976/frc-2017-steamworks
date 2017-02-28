package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.library.outputs.*;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;

public class Outputs {

    public VictorSP driveLeftFront;
    public VictorSP driveLeftRear;
    public VictorSP driveRightFront;
    public VictorSP driveRightRear;

    public CANTalon elevator;
    public CANTalon roller;
    public CANTalon winchMaster;
    public CANTalon shooter;
    public CANTalon pivot;

    public TimedSolenoid gear;
    public TimedSolenoid arch;
    
    public LinearActuator hood;

    public Taloon agitator;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new VictorSP(0);
        driveLeftRear = new VictorSP(1);
        driveRightFront = new VictorSP(2);
        driveRightRear = new VictorSP(3);

        elevator = new CANTalon(3);

        shooter = new CANTalon(12);
        shooter.changeControlMode(CANTalon.TalonControlMode.Speed);
        shooter.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        shooter.reverseSensor(true);
        shooter.configEncoderCodesPerRev(48);

        pivot = new CANTalon(4);

        CANTalon shooterSlave = new CANTalon(13);
        shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        shooterSlave.set(shooter.getDeviceID());

        winchMaster = new CANTalon(6);
        winchMaster.reverseOutput(true);

        CANTalon winchSlave = new CANTalon(7);
        winchSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        winchSlave.set(winchMaster.getDeviceID());

        roller = new CANTalon(2);

        gear = new TimedSolenoid(module, 20, 1, 6);
        arch = new TimedSolenoid(module, 20, 0, 7);

        agitator = new Taloon(5);

        hood = new LinearActuator(4);

        module.addListener(new RobotStateListener() {

            private Compressor compressor = new Compressor(20);

            DigitalOutput ringlight = new DigitalOutput(9);
            Solenoid underGlow = new Solenoid(20, 5);

            @Override public void robotInit() { underGlow.set(true); }

            @Override public void teleopInit() { this.ringlight.set(true); }

            @Override public void disabledInit() {

                this.ringlight.set(false);
                compressor.setClosedLoopControl(false);
            }

            @Override public void testInit() { compressor.setClosedLoopControl(true); }
        });
    }
}
