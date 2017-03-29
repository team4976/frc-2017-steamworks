package ca._4976.steamworks.subsystems;

import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Status {

    private NetworkTable table = NetworkTable.getTable("Status");

    boolean pivotEncoderFunctional = true;

    public Status(Robot robot) {

        Initialization.HARDWARE_INPUT_EVALS.add(() -> {

            table.putNumber("Shooter Target (RPM)", (int) robot.shooter.getTargetRPM());
            table.putNumber("Shooter Speed (RPM)", robot.outputs.shooter.getSpeed());
            table.putNumber("Shooter Error (RPM)", robot.outputs.shooter.getError());
            table.putNumber("Hood Position (%)", robot.outputs.hood.get());
            table.putNumber("Turret Position (UNIT)", robot.outputs.pivot.getPosition());

            table.putNumber("Turret Error (UNITS)", robot.outputs.pivot.getError());
            table.putBoolean("Turret Encoder Functional (BOOL)", pivotEncoderFunctional);

            table.putNumber("Agitator Error (AMPS)", robot.outputs.agitator.getError());

            table.putNumber("Agitator Output (VOLTS)", robot.outputs.agitator.getOutputVoltage());

            table.putNumber("Left Drive Output (%)", robot.outputs.driveLeftFront.get());
            table.putNumber("Right Drive Output (%)", robot.outputs.driveRightFront.get());

            table.putNumber("Left Drive Encoder (METERS)", robot.inputs.driveLeft.getDistance());
            table.putNumber("Right Drive Encoder (METERS)", robot.inputs.driveRight.getDistance());

            table.putNumber("Left Drive Target (METERS)", robot.profiler.playback.getLeftTarget());
            table.putNumber("Right Drive Target (METERS)", robot.profiler.playback.getRightTarget());

            table.putNumber("Left Drive Error (METERS)", robot.profiler.playback.getLeftError());
            table.putNumber("Right Drive Error (METERS)", robot.profiler.playback.getRightError());

            table.putBoolean("Vision Running (BOOLEAN)", robot.vision.isRunning());
            table.putNumber("Vision Error (PIXELS)", robot.vision.getError());
            table.putBoolean("Vision Light (BOOLEAN)", robot.outputs.visionLight.get());

            table.putBoolean("Winch Arm Extended (BOOLEAN)", robot.outputs.arch.isExtended());
            table.putBoolean("Intake Extended (BOOLEAN)", robot.outputs.gear.isExtended());


            CANTalon.FeedbackDeviceStatus status = robot.outputs.pivot.isSensorPresent(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);

            pivotEncoderFunctional = status != CANTalon.FeedbackDeviceStatus.FeedbackStatusNotPresent;
        });
    }
}
