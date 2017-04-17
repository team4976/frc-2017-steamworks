package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

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

    boolean disable = false;

    Playback(Robot robot) {

        this.robot = robot;

        robot.addListener(new RobotStateListener() {

            @Override
            public void disabledInit() {
                disable = false;
            }
        });
    }

    @Override public void run() {

        final long startTime = System.currentTimeMillis();

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        double leftIntegral = 0;
        double rightIntegral = 0;

        double lastLeftError = 0;
        double lastRightError = 0;

        Config.Motion config = robot.config.motion;

        robot.shooter.setTargetRPM(profile.Shooter_RPM);
        robot.outputs.hood.set(profile.Hood_Position);
        robot.outputs.pivot.changeControlMode(CANTalon.TalonControlMode.Position);
        robot.outputs.pivot.set(profile.Turret_Position);

        if (profile.Run_Shooter) robot.shooter.run();

        if (profile.Extend_Winch_Arm) robot.winch.extend();

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();

            String file = "/home/lvuser/motion/logs/Log " + dateFormat.format(date) + ".csv";

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));

            writer.write("Profile " + NetworkTable.getTable("Motion Control").getString("load_table", "") + ",,,,,Actual");
            writer.newLine();
            writer.write("Left Output,Right Output,Left Position,Right Position,Left Velocity,Right Velocity,");
            writer.write("Left Output,Right Output,Left Position,Right Position,Left Error,Right Error");
            writer.newLine();
            writer.flush();

            while (robot.isEnabled() && tickCount < profile.Moments.length) {

                if (System.nanoTime() - lastTickTime >= config.tickTime) {

                    for (int i = 0; i < profile.Evaluable.length; i++) {

                        final int tick = i;

                        if (profile.Evaluate_Timing[i] == tickCount)

                            synchronized (this) {
                                new Thread(() -> profile.Evaluable[tick].eval()).start();
                            }
                    }

                    if (disable) {

                        tickCount++;
                        avgTickRate += System.nanoTime() - lastTickTime;

                        robot.outputs.driveLeftFront.set(0);
                        robot.outputs.driveLeftRear.set(0);

                        robot.outputs.driveRightFront.set(0);
                        robot.outputs.driveRightRear.set(0);

                        continue;
                    }

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
                                    + (config.kP * leftError);
                                    //+ (config.kI * leftIntegral)
                                    //+ (config.kD * leftDerivative);

                    double rightDrive =
                            moment.rightDriveOutput
                                    + (config.kP * rightError);
                                    //+ (config.kI * rightIntegral)
                                    //+ (config.kD * rightDerivative);

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
                    writer.write(actualLeftPosition + ",");
                    writer.write(actualRightPosition + ",");
                    writer.write(leftError + ",");
                    writer.write(rightError + "");

                    writer.newLine();
                    writer.flush();

                    tickCount++;
                    avgTickRate += System.nanoTime() - lastTickTime;
                }
            }

            writer.close();

        } catch (IOException e) { e.printStackTrace(); }

        robot.outputs.driveLeftFront.set(0);
        robot.outputs.driveLeftRear.set(0);

        robot.outputs.driveRightFront.set(0);
        robot.outputs.driveRightRear.set(0);

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3fms", avgTickRate / 1e+6);
        System.out.printf(" %.1f%%%n", (avgTickRate / config.tickTime) * 100);
    }

    void disable() { disable = true; }

    void setProfile(Profile profile) { this.profile = profile; }

    public synchronized double getLeftTarget() { return leftTarget; }

    public synchronized double getRightTarget() { return rightTarget; }

    public synchronized double getLeftError() { return leftError; }

    public synchronized double getRightError() { return rightError; }
}
