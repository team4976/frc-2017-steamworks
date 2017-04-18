package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class DriveTrain {

    private Robot robot;
    private Config.Drive config;
    private double[] targetVelocity = new double[] {0, 0};
    private double[] setVelocity = new double[] {0, 0};
    private double leftTrigger = 0;
    private double rightTrigger = 0;
    private double limiter = 1.0;

    public DriveTrain(Robot robot) {

        this.robot = robot;
        config = robot.config.drive;

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

            targetVelocity[1] = (value > 0 ? value * value : value * -value) * 0.9;

            if (!robot.isTest()) {

                setVelocity[1] = value > 0 ? value * value : value * -value;
                output();
            }
        });

        robot.driver.LT.addListener(value -> {

            leftTrigger = value;
            targetVelocity[0] = (rightTrigger - leftTrigger) * 0.9;

            if (!robot.isTest()) {

                setVelocity[0] = rightTrigger - leftTrigger;
                output();
            }
        });

        robot.driver.RT.addListener(value -> {

            rightTrigger = value;
            targetVelocity[0] = (rightTrigger - leftTrigger) * 0.9;

            if (!robot.isTest()) {

                setVelocity[0] = rightTrigger - leftTrigger;
                output();
            }
        });
    }

    private void output() {

        final double sticktion = 0.1;

        double left = (setVelocity[1] * limiter + setVelocity[0] * limiter);
        left = (left > 0 ? sticktion : -sticktion) + left * (1 - sticktion);

        double right = (setVelocity[1] * limiter - setVelocity[0] * limiter);
        right = (right > 0 ? sticktion : -sticktion) + right * (1 - sticktion);

        if (setVelocity[0] != 0 || setVelocity[1] != 0) {

            robot.outputs.driveLeftFront.set(left);
            robot.outputs.driveLeftRear.set(left);
            robot.outputs.driveRightFront.set(right);
            robot.outputs.driveRightRear.set(right);

        } else {

            robot.outputs.driveLeftFront.set(0);
            robot.outputs.driveLeftRear.set(0);
            robot.outputs.driveRightFront.set(0);
            robot.outputs.driveRightRear.set(0);
        }

    }

    void setLimiter(double limit) { limiter = limit; }

    public void update() {

        double diffX = targetVelocity[0] - setVelocity[0];
        double diffY = targetVelocity[1] - setVelocity[1];

        if ((setVelocity[0] >= 0 && targetVelocity[0] > setVelocity[0]) ||
                (setVelocity[0] <= 0 && targetVelocity[0] < setVelocity[0]))

            if (Math.abs(diffX) <= config.linearRamp[0]) setVelocity[0] = targetVelocity[0];

            else if (setVelocity[0] < targetVelocity[0]) setVelocity[0] = setVelocity[0] + config.linearRamp[0];

            else setVelocity[0] = setVelocity[0] - config.linearRamp[0];

        else if (Math.abs(diffX) <= config.linearRamp[1]) setVelocity[0] = targetVelocity[0];

        else if (setVelocity[0] < targetVelocity[0]) setVelocity[0] = setVelocity[0] + config.linearRamp[1];

        else setVelocity[0] = setVelocity[0] - config.linearRamp[1];


        if ((setVelocity[1] >= 0 && targetVelocity[1] > setVelocity[1]) ||
                (setVelocity[1] <= 0 && targetVelocity[1] < setVelocity[1]))

            if (Math.abs(diffY) <= config.rotationalRamp[0]) setVelocity[1] = targetVelocity[1];

            else if (setVelocity[1] < targetVelocity[1]) setVelocity[1] = setVelocity[1] + config.rotationalRamp[0];

            else setVelocity[1] = setVelocity[1] - config.rotationalRamp[0];

        else if (Math.abs(diffY) <= config.rotationalRamp[1]) setVelocity[1] = targetVelocity[1];

        else if (setVelocity[1] < targetVelocity[1]) setVelocity[1] = setVelocity[1] + config.rotationalRamp[1];

        else setVelocity[1] = setVelocity[1] - config.rotationalRamp[1];

        output();
    }
}
