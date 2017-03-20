package ca._4976.steamworks.subsystems;

import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Status {

    private NetworkTable table = NetworkTable.getTable("Status");

    public Status(Robot robot) {

        Initialization.HARDWARE_INPUT_EVALS.add(() -> {

            table.putNumber("Shooter Speed (RPM)", robot.outputs.shooter.getSpeed());
            table.putNumber("Shooter Error (RPM)", robot.outputs.shooter.getError());

            table.putNumber("Left Drive Output (%)", robot.outputs.driveLeftFront.get());
            table.putNumber("Right Drive Output (%)", robot.outputs.driveRightFront.get());

            table.putNumber("Vision Error (PIXELS)", robot.vision.getError());
        });
    }
}
