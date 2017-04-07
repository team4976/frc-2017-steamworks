package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.Initialization;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.library.outputs.*;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
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

        driveLeftFront = Initialization.DEBUG ? new NetworkVictorSP(0) : new VictorSP(0);
        driveLeftRear = Initialization.DEBUG ? new NetworkVictorSP(1) : new VictorSP(1);
        driveRightFront = Initialization.DEBUG ? new NetworkVictorSP(2) : new VictorSP(2);
        driveRightRear = Initialization.DEBUG ? new NetworkVictorSP(3) : new VictorSP(3);

        elevator = new Taloon(5);

        shooter = Initialization.DEBUG ? new NetworkTalonSRX(12) : new CANTalon(12);
        shooter.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        shooter.reverseSensor(true);
        shooter.configEncoderCodesPerRev(48);

        pivot = Initialization.DEBUG ? new NetworkTalonSRX(4) : new CANTalon(4);
        pivot.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

        shooterSlave = Initialization.DEBUG ? new NetworkTalonSRX(13) : new CANTalon(13);
        shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        shooterSlave.set(12);

        winchMaster = Initialization.DEBUG ? new NetworkTalonSRX(6) : new CANTalon(6);
        winchMaster.reverseOutput(true);

        CANTalon winchSlave = Initialization.DEBUG ? new NetworkTalonSRX(7) : new CANTalon(7);
        winchSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        winchSlave.set(winchMaster.getDeviceID());

        roller = Initialization.DEBUG ? new NetworkTalonSRX(15) : new CANTalon(15);

        gear = Initialization.DEBUG ? new NetworkSolenoid(module, 20, 1, 6) : new TimedSolenoid(module, 20, 1, 6);
        arch = Initialization.DEBUG ? new NetworkSolenoid(module, 20, 0, 7) : new TimedSolenoid(module, 20, 0, 7);

        agitator = Initialization.DEBUG ? new NetworkTalonSRX(2) : new CANTalon(2);
        agitator.reverseOutput(true);

        hood = new LinearActuator(4);

        visionLight = new Solenoid(20, 4);

        module.addListener(new RobotStateListener() {

            private Compressor compressor = new Compressor(20);

            Solenoid underGlow = new Solenoid(20, 5);

            @Override public void robotInit() {

                //System.out.println(pivot.getEncPosition());



                shooter.changeControlMode(CANTalon.TalonControlMode.Speed);
                shooter.set(0);

                underGlow.set(true);
            }

            @Override public void autonomousInit() {

                pivot.setEncPosition(0);
            }

            @Override public void teleopInit() {

                compressor.setClosedLoopControl(true);

            }


            @Override public void testInit() { compressor.setClosedLoopControl(true); }
        });
    }
}
