package ca._4976.steamworks.subsystems;

import ca._4976.steamworks.Robot;

import java.util.ArrayList;

public class MotionControl {

    private Robot module;

    private ArrayList<Moment> moments = new ArrayList<>();

    private double kP = 0, kI = 0, kD = 0;

    public MotionControl(Robot module) { this.module = module; }

    public MotionControl(Robot module, double kP, double kI, double kD) {

        this.module = module;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public MotionControl(Robot module, double kP, double kI, double kD, String name) {

        this.module = module;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        load(name);
    }

    private final double tickTiming = 1000000000 / 200;

    private class Record implements Runnable {

        @Override public void run() {

            moments.clear();

            long lastTickTime = System.nanoTime();
            double avgTickRate = 0;
            int tickCount = 0;

            while (module.isEnabled()) {

                if (System.nanoTime() - lastTickTime >= tickTiming) {

                    lastTickTime = System.nanoTime();
                    tickCount++;

                    moments.add(new Moment(
                            module.outputs.driveLeftFront.get(),
                            module.outputs.driveLeftFront.get(),
                            module.inputs.driveLeft.getDistance(),
                            module.inputs.driveRight.getDistance(),
                            module.inputs.driveLeft.getRate(),
                            module.inputs.driveRight.getRate()
                    ));

                    avgTickRate += System.nanoTime() - lastTickTime;
                }
            }

            avgTickRate /= tickCount;
            System.out.printf("Average tick time: %.3f", avgTickRate);
            System.out.printf("time usage: %%%.1f", tickTiming / avgTickRate);
        }
    }

    private class Playback implements Runnable {

        @Override public void run() {

            long lastTickTime = System.nanoTime();
            double avgTickRate = 0;
            int tickCount = 0;

            double leftIntegral = 0;
            double rightIntegral = 0;

            double lastLeftError = 0;
            double lastRightError = 0;

            while (module.isEnabled() && tickCount < moments.size()) {

                if (System.nanoTime() - lastTickTime >= tickTiming) {

                    lastTickTime = System.nanoTime();

                    Moment moment = moments.get(tickCount);

                    double actualLeftPosition = module.inputs.driveLeft.getDistance();
                    double actualRightPosition = module.inputs.driveRight.getDistance();

                    double leftError = moment.leftEncoderPosition - actualLeftPosition;
                    double rightError = moment.rightEncoderPosition - actualRightPosition;

                    leftIntegral += leftError * tickTiming;
                    rightIntegral += rightError * tickTiming;

                    double leftDerivative = (leftError - lastLeftError) / tickTiming - moment.leftEncoderVelocity;
                    double rightDerivative = (rightError - lastRightError) / tickTiming - moment.rightEncoderVelocity;

                    updateLeftDrive(
                            moment.leftDriveOutput
                                    + kP * leftError
                                    + kI * leftIntegral
                                    + kD * leftDerivative
                    );

                    updateRightDrive(
                            moment.rightDriveOutput
                                    + kP * rightError
                                    + kI * rightIntegral
                                    + kD * rightDerivative
                    );

                    lastLeftError = leftError;
                    lastRightError = rightError;

                    tickCount++;
                    avgTickRate += System.nanoTime() - lastTickTime;
                }
            }

            avgTickRate /= tickCount;
            System.out.printf("Average tick time: %.3f", avgTickRate);
            System.out.printf("time usage: %%%.1f", tickTiming / avgTickRate);
        }
    }

    private class Moment {

        private final double leftDriveOutput;
        private final double rightDriveOutput;

        private final double leftEncoderPosition;
        private final double rightEncoderPosition;

        private final double leftEncoderVelocity;
        private final double rightEncoderVelocity;

        public Moment(
                double leftDriveOutput,
                double rightDriveOutput,
                double leftEncoderPosition,
                double rightEncoderPosition,
                double leftEncoderVelocity,
                double rightEncoderVelocity
        ) {
            this.leftDriveOutput = leftDriveOutput;
            this.rightDriveOutput = rightDriveOutput;
            this.leftEncoderPosition = leftEncoderPosition;
            this.rightEncoderPosition = rightEncoderPosition;
            this.leftEncoderVelocity = leftEncoderVelocity;
            this.rightEncoderVelocity = rightEncoderVelocity;
        }
    }

    private void updateLeftDrive(double speed) {

        module.outputs.driveLeftFront.pidWrite(speed);
        module.outputs.driveLeftRear.pidWrite(speed);
    }

    private void updateRightDrive(double speed) {

        module.outputs.driveRightFront.pidWrite(speed);
        module.outputs.driveRightRear.pidWrite(speed);
    }

    public void setPID(double kP, double kI, double kD) { }

    public synchronized void record() { new Thread(new Record()).start(); }

    public synchronized void play() { new Thread(new Playback()).start(); }

    public void save(String name) { } //TODO Add write to file

    public void load(String name) { } //TODO Add read from file
}
