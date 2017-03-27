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

    public Taloon elevator;
    public CANTalon roller;
    public CANTalon winchMaster;
    public CANTalon shooter;
    public CANTalon shooterSlave;
    public CANTalon pivot;

    public TimedSolenoid gear;
    public TimedSolenoid arch;
    
    public LinearActuator hood;

    public CANTalon agitator;

    public Solenoid visionLight;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new VictorSP(0);
        driveLeftRear = new VictorSP(1);
        driveRightFront = new VictorSP(2);
        driveRightRear = new VictorSP(3);

        elevator = new Taloon(5);

        shooter = new CANTalon(12);
        shooter.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        shooter.reverseSensor(true);
        shooter.configEncoderCodesPerRev(48);

        pivot = new CANTalon(4);

        shooterSlave = new CANTalon(13);
        shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        shooterSlave.set(12);

        winchMaster = new CANTalon(6);
        winchMaster.reverseOutput(true);

        CANTalon winchSlave = new CANTalon(7);
        winchSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        winchSlave.set(winchMaster.getDeviceID());

        roller = new CANTalon(15);

        gear = new TimedSolenoid(module, 20, 1, 6);
        arch = new TimedSolenoid(module, 20, 0, 7);

        agitator = new CANTalon(2);

        hood = new LinearActuator(4);

        visionLight = new Solenoid(20, 4);

        module.addListener(new RobotStateListener() {

            private Compressor compressor = new Compressor(20);

            Solenoid underGlow = new Solenoid(20, 5);

            @Override public void robotInit() {

                //System.out.println(pivot.getEncPosition());

//                //pivot.setForwardSoftLimit(pivot.getEncPosition() + 300);
//                //pivot.setReverseSoftLimit(pivot.getEncPosition() - 300);
//
//                pivot.enableForwardSoftLimit(true);
//                pivot.enableReverseSoftLimit(true);


                pivot.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
                shooter.changeControlMode(CANTalon.TalonControlMode.Speed);
                shooter.set(0);

                underGlow.set(true);
            }

            @Override public void teleopInit() {

                compressor.setClosedLoopControl(true);

            }


            @Override public void testInit() { compressor.setClosedLoopControl(true); }
        });
    }
}
