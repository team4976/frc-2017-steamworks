package ca._4976.steamworks.subsystems.profiler;

import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import com.ctre.CANTalon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        robot.shooter.setTargetRPM(profile.Shooter_RPM);
        robot.outputs.hood.set(profile.Hood_Position);
        robot.outputs.pivot.changeControlMode(CANTalon.TalonControlMode.Position);
        robot.outputs.pivot.set(profile.Turret_Position);

        if (profile.Run_Shooter) robot.shooter.run();

        if (profile.Extend_Winch_Arm) robot.winch.extend();

        try {

            long startTime = System.currentTimeMillis() / 1000;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();

            String file = "/home/lvuser/motion/logs/Log " + dateFormat.format(date) + ".csv";

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));

            while (robot.isEnabled() && tickCount < profile.Moments.length) {

                if (System.nanoTime() - lastTickTime >= config.tickTime) {

                    if (profile.Disable_Motion > 0)
                        if (System.currentTimeMillis() / 1000 - startTime >= profile.Disable_Motion) break;

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
                                    + (config.kD * leftDerivative);

                    double rightDrive =
                            moment.rightDriveOutput
                                    + (config.kP * rightError)
                                    + (config.kI * rightIntegral)
                                    + (config.kD * rightDerivative);

                    robot.outputs.driveLeftFront.set(leftDrive);
                    robot.outputs.driveLeftRear.set(leftDrive);

                    robot.outputs.driveRightFront.set(rightDrive);
                    robot.outputs.driveRightRear.set(rightDrive);

                    lastLeftError = leftError;
                    lastRightError = rightError;

                    writer.write(moment.leftDriveOutput + ",");
                    writer.write(moment.rightDriveOutput + ",");
                    writer.write(moment.leftEncoderPosition + ",");
                    writer.write(moment.rightEncoderPosition + ",");
                    writer.write(moment.leftEncoderVelocity + ",");
                    writer.write(moment.rightEncoderVelocity + ",");
                    writer.write(leftDrive + ",");
                    writer.write(rightDrive + ",");
                    writer.write(leftError * config.kP + ",");
                    writer.write(rightError * config.kP + ",");
                    writer.write(leftIntegral * config.kI + ",");
                    writer.write(rightIntegral * config.kI + ",");
                    writer.write(leftDerivative * config.kD + ",");
                    writer.write(rightDerivative * config.kD + "");

                    writer.newLine();

                    tickCount++;
                    avgTickRate += System.nanoTime() - lastTickTime;
                }
            }

        } catch (IOException e) { e.printStackTrace(); }

        robot.outputs.driveLeftFront.set(0);
        robot.outputs.driveLeftRear.set(0);

        robot.outputs.driveRightFront.set(0);
        robot.outputs.driveRightRear.set(0);

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3fms", avgTickRate / 1e+6);
        System.out.printf(" %.1f%%%n", (avgTickRate / config.tickTime) * 100);
    }

    void setProfile(Profile profile) { this.profile = profile; }

    public synchronized double getLeftTarget() { return leftTarget; }

    public synchronized double getRightTarget() { return rightTarget; }

    public synchronized double getLeftError() { return leftError; }

    public synchronized double getRightError() { return rightError; }
}
