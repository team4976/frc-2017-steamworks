package ca._4976.steamworks.subsystems;

import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Status {

    private NetworkTable table = NetworkTable.getTable("Status");

    boolean pivotEncoderFunctional = true;

    public Status(Robot robot) {

        Initialization.HARDWARE_INPUT_EVALS.add(() -> {

            table.putNumber("Shooter Target (RPM)", robot.shooter.config.targetSpeed[robot.shooter.selection]);
            table.putNumber("Shooter Speed (RPM)", robot.outputs.shooter.getSpeed());
            table.putNumber("Shooter Error (RPM)", robot.outputs.shooter.getError());

            table.putNumber("Turret Error (UNITS)", robot.outputs.pivot.getError());
            table.putBoolean("Turret Encoder Functional (BOOL)", pivotEncoderFunctional);

            table.putNumber("Agitator Error (AMPS)", robot.outputs.agitator.getError());

            table.putNumber("Agitator Output (VOLTS)", robot.outputs.agitator.getOutputVoltage());

            table.putNumber("Left Drive Output (%)", robot.outputs.driveLeftFront.get());
            table.putNumber("Right Drive Output (%)", robot.outputs.driveRightFront.get());

            table.putNumber("Vision Error (PIXELS)", robot.vision.getError());

            if (Math.abs(robot.outputs.pivot.getOutputVoltage()) > 1)
                pivotEncoderFunctional = robot.outputs.pivot.getSpeed() != 0;
        });
    }
}
