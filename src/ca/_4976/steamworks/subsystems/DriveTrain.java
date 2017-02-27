package ca._4976.steamworks.subsystems;

import ca._4976.library.math.Vector2D;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DriveTrain {

    private Vector2D targetVelocity = new Vector2D(0, 0);
    private Vector2D setVelocity = new Vector2D(0, 0);

    private Vector2D linearRamp = new Vector2D(0.04, 0.06);
    private Vector2D rotationalRamp = new Vector2D(0.08, 0.1);

    private Robot robot;

    public DriveTrain(Robot robot) {

        this.robot = robot;

        robot.driver.LH.addListener(value -> {

            if (!robot.isTest()) {

                targetVelocity.setY(value > 0 ? value * value : value * -value);
                output();
            }
        });

        robot.driver.BT.addListener(value -> {

            if (!robot.isTest()) {

                targetVelocity.setX(value);
                output();
            }
        });
    }

    private void output() {

        robot.outputs.driveLeftFront.set(setVelocity.getY() + setVelocity.getX());
        robot.outputs.driveLeftRear.set(setVelocity.getY() + setVelocity.getX());
        robot.outputs.driveRightFront.set(setVelocity.getY() - setVelocity.getX());
        robot.outputs.driveRightRear.set(setVelocity.getY() - setVelocity.getX());
    }

    public void update() {

        double diffX = targetVelocity.getX() - setVelocity.getX();
        double diffY = targetVelocity.getY() - setVelocity.getY();

        if ((setVelocity.getX() >= 0 && targetVelocity.getX() > setVelocity.getX()) ||
                (setVelocity.getX() <= 0 && targetVelocity.getX() < setVelocity.getX()))

            if (Math.abs(diffX) <= linearRamp.getX()) setVelocity.setX(targetVelocity.getX());

            else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + linearRamp.getX());

            else setVelocity.setX(setVelocity.getX() - linearRamp.getX());

        else if (Math.abs(diffX) <= linearRamp.getY()) setVelocity.setX(targetVelocity.getX());

        else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + linearRamp.getY());

        else setVelocity.setX(setVelocity.getX() - linearRamp.getY());


        if ((setVelocity.getY() >= 0 && targetVelocity.getY() > setVelocity.getY()) ||
                (setVelocity.getY() <= 0 && targetVelocity.getY() < setVelocity.getY()))

            if (Math.abs(diffY) <= rotationalRamp.getX()) setVelocity.setY(targetVelocity.getY());

            else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + rotationalRamp.getX());

            else setVelocity.setY(setVelocity.getY() - rotationalRamp.getX());

        else if (Math.abs(diffY) <= rotationalRamp.getY()) setVelocity.setY(targetVelocity.getY());

        else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + rotationalRamp.getY());

        else setVelocity.setY(setVelocity.getY() - rotationalRamp.getY());

        output();

        NetworkTable.getTable("status").putNumber("drive_linear", setVelocity.getX());
        NetworkTable.getTable("status").putNumber("drive_rotational", setVelocity.getY());
    }
}
