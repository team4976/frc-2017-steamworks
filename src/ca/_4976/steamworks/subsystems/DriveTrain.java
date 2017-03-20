package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.library.math.Vector2D;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DriveTrain {

    private Vector2D targetVelocity = new Vector2D(0, 0);
    private Vector2D setVelocity = new Vector2D(0, 0);

    private Config config = Config.getInstance();

    private double leftTrigger = 0;
    private double rightTrigger = 0;

    private Robot robot;

    public DriveTrain(Robot robot) {

        this.robot = robot;

        robot.addListener(new RobotStateListener() {
            @Override
            public void disabledInit() {

                robot.outputs.driveLeftFront.set(0);
                robot.outputs.driveRightFront.set(0);
                robot.outputs.driveLeftRear.set(0);
                robot.outputs.driveRightRear.set(0);
            }
        });

        robot.driver.LH.addListener(value -> {

            targetVelocity.setY(value > 0 ? value * value : value * -value);

            if (!robot.isTest()) {

                setVelocity.setY(value > 0 ? value * value : value * -value);
                output();
            }
        });

        robot.driver.LT.addListener(value -> {

            leftTrigger = value;
            targetVelocity.setX(rightTrigger - leftTrigger);

            if (!robot.isTest()) {

                setVelocity.setX(rightTrigger - leftTrigger);
                output();
            }
        });

        robot.driver.RT.addListener(value -> {

            rightTrigger = value;
            targetVelocity.setX(rightTrigger - leftTrigger);

            if (!robot.isTest()) {

                setVelocity.setX(rightTrigger - leftTrigger);
                output();
            }
        });
    }

    private void output() {

        robot.outputs.driveLeftFront.set(targetVelocity.getY() + targetVelocity.getX());
        robot.outputs.driveLeftRear.set(targetVelocity.getY() + targetVelocity.getX());
        robot.outputs.driveRightFront.set(targetVelocity.getY() - targetVelocity.getX());
        robot.outputs.driveRightRear.set(targetVelocity.getY() - targetVelocity.getX());
    }

    public void update() {

        double diffX = targetVelocity.getX() - setVelocity.getX();
        double diffY = targetVelocity.getY() - setVelocity.getY();

        if ((setVelocity.getX() >= 0 && targetVelocity.getX() > setVelocity.getX()) ||
                (setVelocity.getX() <= 0 && targetVelocity.getX() < setVelocity.getX()))

            if (Math.abs(diffX) <= config.drive.linearRamp.getX()) setVelocity.setX(targetVelocity.getX());

            else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + config.drive.linearRamp.getX());

            else setVelocity.setX(setVelocity.getX() - config.drive.linearRamp.getX());

        else if (Math.abs(diffX) <= config.drive.linearRamp.getY()) setVelocity.setX(targetVelocity.getX());

        else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + config.drive.linearRamp.getY());

        else setVelocity.setX(setVelocity.getX() - config.drive.linearRamp.getY());


        if ((setVelocity.getY() >= 0 && targetVelocity.getY() > setVelocity.getY()) ||
                (setVelocity.getY() <= 0 && targetVelocity.getY() < setVelocity.getY()))

            if (Math.abs(diffY) <= config.drive.rotationalRamp.getX()) setVelocity.setY(targetVelocity.getY());

            else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + config.drive.rotationalRamp.getX());

            else setVelocity.setY(setVelocity.getY() - config.drive.rotationalRamp.getX());

        else if (Math.abs(diffY) <= config.drive.rotationalRamp.getY()) setVelocity.setY(targetVelocity.getY());

        else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + config.drive.rotationalRamp.getY());

        else setVelocity.setY(setVelocity.getY() - config.drive.rotationalRamp.getY());

        output();

        NetworkTable.getTable("Status").putNumber("drive_linear", setVelocity.getX());
        NetworkTable.getTable("Status").putNumber("drive_rotational", setVelocity.getY());
    }
}
