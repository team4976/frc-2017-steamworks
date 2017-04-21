package ca._4976.steamworks.subsystems.drive;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class DriveTrain {

    private Robot robot;
    private Config config = new Config();
    private double[] targetVelocity = new double[] {0, 0};
    private double[] setVelocity = new double[] {0, 0};
    private double leftTrigger = 0;
    private double rightTrigger = 0;
    private double limiter = 1.0;
    private boolean disabled = false;

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

            if (!disabled) targetVelocity[1] = (value > 0 ? value * value : value * -value) * 0.9;

            if (!robot.isTest() && !disabled) {

                setVelocity[1] = value > 0 ? value * value : value * -value;
                output();
            }
        });

        robot.driver.LT.addListener(value -> {

            leftTrigger = value;
            if (!disabled) targetVelocity[0] = (rightTrigger - leftTrigger) * 0.9;

            if (!robot.isTest() && !disabled) {

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

        double left = (setVelocity[1] * limiter + setVelocity[0] * limiter);
        left = (left > 0 ? config.sticktion : -config.sticktion) + left * (1 - config.sticktion);

        double right = (setVelocity[1] * limiter - setVelocity[0] * limiter);
        right = (right > 0 ? config.sticktion : -config.sticktion) + right * (1 - config.sticktion);

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

    public void disableUserControl(boolean disabled) {

            this.disabled = disabled;
    }

    public void arcadeDrive(double forward, double turn) {

        targetVelocity[0] = forward;
        targetVelocity[1] = turn;
    }

    public void setLimiter(double limit) { limiter = limit; }

    public void update() {

        double diffX = targetVelocity[0] - setVelocity[0];
        double diffY = targetVelocity[1] - setVelocity[1];

        if ((setVelocity[0] >= 0 && targetVelocity[0] > setVelocity[0]) ||
                (setVelocity[0] <= 0 && targetVelocity[0] < setVelocity[0]))

            if (Math.abs(diffX) <= config.linearRamp[0]) setVelocity[0] = targetVelocity[0];

            else if (setVelocity[0] < targetVelocity[0])
                setVelocity[0] = setVelocity[0] + config.linearRamp[0];

            else setVelocity[0] = setVelocity[0] - config.linearRamp[0];

        else if (Math.abs(diffX) <= config.linearRamp[1]) setVelocity[0] = targetVelocity[0];

        else if (setVelocity[0] < targetVelocity[0])
            setVelocity[0] = setVelocity[0] + config.linearRamp[1];

        else setVelocity[0] = setVelocity[0] - config.linearRamp[1];


        if ((setVelocity[1] >= 0 && targetVelocity[1] > setVelocity[1]) ||
                (setVelocity[1] <= 0 && targetVelocity[1] < setVelocity[1]))

            if (Math.abs(diffY) <= config.rotationalRamp[0]) setVelocity[1] = targetVelocity[1];

            else if (setVelocity[1] < targetVelocity[1])
                setVelocity[1] = setVelocity[1] + config.rotationalRamp[0];

            else setVelocity[1] = setVelocity[1] - config.rotationalRamp[0];

        else if (Math.abs(diffY) <= config.rotationalRamp[1])
            setVelocity[1] = targetVelocity[1];

        else if (setVelocity[1] < targetVelocity[1])
            setVelocity[1] = setVelocity[1] + config.rotationalRamp[1];

        else setVelocity[1] = setVelocity[1] - config.rotationalRamp[1];

        output();
    }
}
