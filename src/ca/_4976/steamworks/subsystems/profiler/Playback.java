package ca._4976.steamworks.subsystems.profiler;

import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;

public class Playback implements Runnable {

    private final Robot robot;

    private Profile profile = null;

    private double leftTarget;
    private double rightTarget;

    private double leftError;
    private double rightError;

    Playback(Robot robot) { this.robot = robot; }

    @Override public void run() {

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        double leftIntegral = 0;
        double rightIntegral = 0;

        double lastLeftError = 0;
        double lastRightError = 0;

        Config.Motion config = robot.config.motion;

        synchronized (this) { new Thread(() -> {

            for (int i = 0; i < profile.Evaluable.length; i++)
                robot.runNextLoop(profile.Evaluable[i], profile.Evaluate_Timing[i]);

        }).start(); }

        while (robot.isEnabled() && tickCount < profile.Moments.length) {

            if (System.nanoTime() - lastTickTime >= config.tickTime) {

                lastTickTime = System.nanoTime();

                Moment moment = profile.Moments[tickCount];

                leftTarget = moment.leftEncoderPosition;
                rightTarget = moment.rightEncoderPosition;

                double actualLeftPosition = robot.inputs.driveLeft.getDistance();
                double actualRightPosition = robot.inputs.driveRight.getDistance();

                leftError = actualLeftPosition - moment.leftEncoderPosition;
                rightError = actualRightPosition - moment.rightEncoderPosition;

                leftIntegral += leftError * config.tickTime;
                rightIntegral += rightError * config.tickTime;

                double leftDerivative = (leftError - lastLeftError) / config.tickTime - moment.leftEncoderVelocity;
                double rightDerivative = (rightError - lastRightError) / config.tickTime - moment.rightEncoderVelocity;

                double leftDrive =
                        moment.leftDriveOutput
                                + (config.kP * leftError)
                                + (config.kI * leftIntegral)
                                + (config.kD * leftDerivative)
                        ;

                double rightDrive =
                        moment.rightDriveOutput
                                + (config.kP * rightError)
                                + (config.kI * rightIntegral)
                                + (config.kD * rightDerivative)
                          ;

                robot.outputs.driveLeftFront.set(leftDrive);
                robot.outputs.driveLeftRear.set(leftDrive);

                robot.outputs.driveRightFront.set(rightDrive);
                robot.outputs.driveRightRear.set(rightDrive);
                
                lastLeftError = leftError;
                lastRightError = rightError;

                tickCount++;
                avgTickRate += System.nanoTime() - lastTickTime;
            }
        }

        robot.outputs.driveLeftFront.set(0);
        robot.outputs.driveLeftRear.set(0);

        robot.outputs.driveRightFront.set(0);
        robot.outputs.driveRightRear.set(0);

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %.1f%%%n", config.tickTime / avgTickRate);
    }

    void setProfile(Profile profile) { this.profile = profile; }

    public synchronized double getLeftTarget() { return leftTarget; }

    public synchronized double getRightTarget() { return rightTarget; }

    public synchronized double getLeftError() { return leftError; }

    public synchronized double getRightError() { return rightError; }
}
