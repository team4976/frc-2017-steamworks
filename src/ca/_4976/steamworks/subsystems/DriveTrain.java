package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.library.math.Vector2D;
import ca._4976.steamworks.Robot;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DriveTrain {

    private Vector2D targetVelocity = new Vector2D(0, 0);
    private Vector2D setVelocity = new Vector2D(0, 0);

    private Config config = new Config();

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

            targetVelocity.setY((value > 0 ? value * value : value * -value) * 0.9);

            if (!robot.isTest()) {

                System.out.println("hello");

                setVelocity.setY(value > 0 ? value * value : value * -value);
                output();
            }
        });

        robot.driver.LT.addListener(value -> {

            leftTrigger = value;
            targetVelocity.setX((rightTrigger - leftTrigger) * 0.9);

            if (!robot.isTest()) {

                setVelocity.setX(rightTrigger - leftTrigger);
                output();
            }
        });

        robot.driver.RT.addListener(value -> {

            rightTrigger = value;
            targetVelocity.setX((rightTrigger - leftTrigger) * 0.9);

            if (!robot.isTest()) {

                setVelocity.setX(rightTrigger - leftTrigger);
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

            if (Math.abs(diffX) <= config.linearRamp.getX()) setVelocity.setX(targetVelocity.getX());

            else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + config.linearRamp.getX());

            else setVelocity.setX(setVelocity.getX() - config.linearRamp.getX());

        else if (Math.abs(diffX) <= config.linearRamp.getY()) setVelocity.setX(targetVelocity.getX());

        else if (setVelocity.getX() < targetVelocity.getX()) setVelocity.setX(setVelocity.getX() + config.linearRamp.getY());

        else setVelocity.setX(setVelocity.getX() - config.linearRamp.getY());


        if ((setVelocity.getY() >= 0 && targetVelocity.getY() > setVelocity.getY()) ||
                (setVelocity.getY() <= 0 && targetVelocity.getY() < setVelocity.getY()))

            if (Math.abs(diffY) <= config.rotationalRamp.getX()) setVelocity.setY(targetVelocity.getY());

            else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + config.rotationalRamp.getX());

            else setVelocity.setY(setVelocity.getY() - config.rotationalRamp.getX());

        else if (Math.abs(diffY) <= config.rotationalRamp.getY()) setVelocity.setY(targetVelocity.getY());

        else if (setVelocity.getY() < targetVelocity.getY()) setVelocity.setY(setVelocity.getY() + config.rotationalRamp.getY());

        else setVelocity.setY(setVelocity.getY() - config.rotationalRamp.getY());

        output();
    }

    private class Config {

        private NetworkTable table = NetworkTable.getTable("Drive");

        private Vector2D linearRamp = new Vector2D(1.0 / 200, 1.0 / 200);
        private Vector2D rotationalRamp = new Vector2D(1.0 / 200, 1.0 / 200);

        private Config() {

            if (table.containsKey("Linear Ramp")) {

                Double[] values = table.getNumberArray("Linear Ramp", new Double[2]);
                linearRamp.setX(values[0] / 200.0);
                linearRamp.setY(values[1] / 200.0);

            } else {

                table.putNumberArray("Linear Ramp", new Double[] { 1.0, 1.0 });
                linearRamp.setX(1.0 / 200.0);
                linearRamp.setY(1.0/ 200.0);
            }

            if (table.containsKey("Rotational Ramp")) {

                Double[] values = table.getNumberArray("Rotational Ramp", new Double[] { 1.0, 1.0 });
                rotationalRamp.setX(values[0] / 200.0);
                rotationalRamp.setY(values[1] / 200.0);

            } else {

                table.putNumberArray("Rotational Ramp", new Double[] { 1.0, 1.0 });
                rotationalRamp.setX(1 / 200.0);
                rotationalRamp.setY(1 / 200.0);
            }

            table.addTableListener((source, key, value, isNew) -> {

                switch (key) {

                    case "Linear Ramp":

                        linearRamp.setX(((Double[]) value)[0] / 200.0);
                        linearRamp.setY(((Double[]) value)[1] / 200.0);

                        break;

                    case "Rotational Ramp":

                        rotationalRamp.setX(((Double[]) value)[0] / 200.0);
                        rotationalRamp.setY(((Double[]) value)[1] / 200.0);

                        break;
                }
            });
        }
    }
}
