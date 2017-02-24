package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.Axis;
import ca._4976.library.controllers.Button;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

class Playback implements Runnable {

    private final Robot robot;

    private Moment[] moments = new Moment[0];

    Playback(Robot robot) { this.robot = robot; }

    @Override public void run() {

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        double leftIntegral = 0;
        double rightIntegral = 0;

        double lastLeftError = 0;
        double lastRightError = 0;

        Config config = Config.getInstance();

        while (robot.isEnabled() && tickCount < moments.length) {

            if (System.nanoTime() - lastTickTime >= config.tickTime) {

                lastTickTime = System.nanoTime();

                Moment moment = moments[tickCount];

                double actualLeftPosition = robot.inputs.driveLeft.getDistance();
                double actualRightPosition = robot.inputs.driveRight.getDistance();

                double leftError = moment.leftEncoderPosition - actualLeftPosition;
                double rightError = moment.rightEncoderPosition - actualRightPosition;

                leftIntegral += leftError * config.tickTime;
                rightIntegral += rightError * config.tickTime;

                double leftDerivative = (leftError - lastLeftError) / config.tickTime - moment.leftEncoderVelocity;
                double rightDerivative = (rightError - lastRightError) / config.tickTime - moment.rightEncoderVelocity;

                double leftDrive =
                        moment.leftDriveOutput
                                + config.kP * leftError
                                + config.kI * leftIntegral
                                + config.kD * leftDerivative;

                double rightDrive =
                        moment.rightDriveOutput
                                + config.kP * rightError
                                + config.kI * rightIntegral
                                + config.kD * rightDerivative;

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

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %%%.1f", config.tickTime / avgTickRate);
    }

    void setProfile(Moment[] moments) { this.moments = moments; }
}
