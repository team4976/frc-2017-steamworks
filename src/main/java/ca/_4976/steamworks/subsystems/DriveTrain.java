package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.math.Vector2D;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DriveTrain {

    public DriveTrain(Robot module) {

        Vector2D targetVelocity = new Vector2D(0, 0);
        Vector2D setVelocity = new Vector2D(0, 0);

        Vector2D linearRamp = new Vector2D(0.04, 0.06);
        Vector2D rotationalRamp = new Vector2D(0.08, 0.1);

        Evaluable updateDrive = new Evaluable() {

            @Override public void eval() {

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

                NetworkTable.getTable("status").putNumber("drive_linear", setVelocity.getX());
                NetworkTable.getTable("status").putNumber("drive_rotational", setVelocity.getY());

                module.outputs.driveLeftFront.set(setVelocity.getY() + setVelocity.getX());
                module.outputs.driveLeftRear.set(setVelocity.getY() + setVelocity.getX());
                module.outputs.driveRightFront.set(setVelocity.getY() - setVelocity.getX());
                module.outputs.driveRightRear.set(setVelocity.getY() - setVelocity.getX());

                //if (setVelocity.getY() != targetVelocity.getY() || setVelocity.getX() != targetVelocity.getX())
                    module.runNextLoop(this);
            }
        };

        module.driver.LH.addListener(value -> {

           // if (setVelocity.getY() != targetVelocity.getY()) {

                targetVelocity.setY(value > 0 ? value * value : value * -value);
            //    updateDrive.eval();

           // } else targetVelocity.setY(value > 0 ? value * value : value * -value);
        });

        module.driver.BT.addListener(value -> {

            //if (setVelocity.getX() != targetVelocity.getX()) {

                targetVelocity.setX(value);
            //    updateDrive.eval();

            //} else targetVelocity.setX(value);
        });

        module.runNextLoop(updateDrive);
    }
}
