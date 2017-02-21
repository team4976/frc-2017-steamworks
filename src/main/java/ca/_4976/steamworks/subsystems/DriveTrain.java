package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.math.Vector2D;
import ca._4976.steamworks.Robot;

public class DriveTrain {

    public DriveTrain(Robot module) {

        Vector2D targetVelocity = new Vector2D(0, 0);
        Vector2D setVelocity = new Vector2D(0, 0);

        Vector2D linearRamp = new Vector2D(0.01, 0.02);
        Vector2D rotationalRamp = new Vector2D(0.02, 0.03);

        Evaluable updateDrive = new Evaluable() {

            @Override public void eval() {

                double diffX = targetVelocity.getX() - setVelocity.getX();
                double diffY = targetVelocity.getY() - setVelocity.getY();

                if ((setVelocity.getX() >= 0 && targetVelocity.getX() > setVelocity.getX()) ||
                        (setVelocity.getX() <= 0 && targetVelocity.getX() < setVelocity.getX()))

                    if (Math.abs(diffX) <= linearRamp.getX()) setVelocity.setX(targetVelocity.getX());

                    else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + linearRamp.getX());

                    else setVelocity.setX(setVelocity.getX() - linearRamp.getX());

                else

                if (Math.abs(diffX) <= linearRamp.getY()) setVelocity.setX(targetVelocity.getX());

                else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + linearRamp.getY());

                else setVelocity.setX(setVelocity.getX() - linearRamp.getY());

                if ((setVelocity.getY() >= 0 && targetVelocity.getY() > setVelocity.getY()) ||
                        (setVelocity.getY() <= 0 && targetVelocity.getY() < setVelocity.getY()))

                    if (Math.abs(diffY) <= rotationalRamp.getX()) setVelocity.setY(targetVelocity.getY());

                    else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + rotationalRamp.getX());

                    else setVelocity.setY(setVelocity.getY() - rotationalRamp.getX());

                else

                if (Math.abs(diffY) <= rotationalRamp.getY()) setVelocity.setY(targetVelocity.getY());

                else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + rotationalRamp.getY());

                else setVelocity.setY(setVelocity.getY() - rotationalRamp.getY());

                module.outputs.driveLeftFront.set(-setVelocity.getX() - setVelocity.getY());
                module.outputs.driveLeftRear.set(-setVelocity.getX() - setVelocity.getY());
                module.outputs.driveRightFront.set(-setVelocity.getX() + setVelocity.getY());
                module.outputs.driveRightRear.set(-setVelocity.getX() + setVelocity.getY());

                module.runNextLoop(this);
            }
        };

        module.driver.LH.addListener(value -> {

            if (targetVelocity.getX() != targetVelocity.getX()) {

                targetVelocity.setX(value > 0 ? value * value : value * -value);
                updateDrive.eval();

            } else targetVelocity.setX(value > 0 ? value * value : value * -value);
        });

        module.driver.BT.addListener(value -> {

            if (targetVelocity.getY() != targetVelocity.getY()) {

                targetVelocity.setY(value);
                updateDrive.eval();

            } else targetVelocity.setX(value);
        });
    }
}
