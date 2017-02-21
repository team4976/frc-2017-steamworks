package ca._4976.steamworks.io;

import ca._4976.library.AsynchronousRobot;

import ca._4976.library.Evaluable;
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

    public CANTalon hopperElevator;
    public CANTalon shooterElevator;

    public CANTalon winchMaster;

    public CANTalon shooterMaster;

    public TimmedSolenoid gearDoor;
    public TimmedSolenoid winchArm;

    public CANTalon turret;

    public LinearActuator shooterHood;

    public Tallon agitator;

    public Outputs(AsynchronousRobot module) {

        driveLeftFront = new VictorSP(0);
        driveLeftRear = new VictorSP(1);
        driveRightFront = new VictorSP(2);
        driveRightRear = new VictorSP(3);

        hopperElevator = new CANTalon(2);
        shooterElevator = new CANTalon(3);

        shooterMaster = new CANTalon(12);
        shooterMaster.changeControlMode(CANTalon.TalonControlMode.Speed);
        shooterMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        shooterMaster.reverseSensor(true);
        shooterMaster.configEncoderCodesPerRev(48);

        turret = new CANTalon(4);

        CANTalon shooterSlave = new CANTalon(13);
        shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        shooterSlave.set(shooterMaster.getDeviceID());

        winchMaster = new CANTalon(6);
        winchMaster.reverseOutput(true);

        CANTalon winchSlave = new CANTalon(7);
        winchSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        winchSlave.set(winchMaster.getDeviceID());

        gearDoor = new TimmedSolenoid(module, 20, 1, 6);
        winchArm = new TimmedSolenoid(module, 20, 0, 7);

        shooterHood = new LinearActuator(4);

        Solenoid leds = new Solenoid(20, 5);

        Evaluable evaluable = new Evaluable() {

            @Override public void eval() {

                leds.set(!leds.get());

                module.runNextLoop(this, (int) (Math.random() * 800) + 100);
            }
        };

        module.runNextLoop(evaluable);

        agitator = new Tallon(5);

        module.addListener(new RobotStateListener() {

            private Compressor compressor = new Compressor(20);

            @Override public void disabledInit() {

                compressor.setClosedLoopControl(false); }

            @Override public void testInit() { compressor.setClosedLoopControl(true); }
        });
    }
}
