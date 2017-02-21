package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

import java.io.*;
import java.util.ArrayList;

public class MotionControl {

    private Robot module;

    private ArrayList<Moment> moments = new ArrayList<>();

    private double kP = 0, kI = 0, kD = 0;

    public MotionControl(Robot module) {

        this.module = module;

        module.addListener(new RobotStateListener() {

            @Override public void autonomousInit() { play(); }

            @Override public void testInit() {

                if (module.operator.BACK.get() || module.driver.BACK.get()) {

                    module.enableOperatorControl();
                    module.runNextLoop(() -> record());
                }

                save("motion-" + System.currentTimeMillis());
            }
        });
    }

    private final double tickTiming = 1000000000 / 200;

    private class Record implements Runnable {

        @Override public void run() {

            moments.clear();

            long lastTickTime = System.nanoTime();
            double avgTickRate = 0;
            int tickCount = 0;

            module.inputs.driveLeft.reset();
            module.inputs.driveRight.reset();

            while (module.isEnabled()) {

                if (System.nanoTime() - lastTickTime >= tickTiming) {

                    lastTickTime = System.nanoTime();
                    tickCount++;

                    boolean[] driverButtons = new boolean[module.driver.buttons.length];
                    for (int i = 0; i < driverButtons.length; i++) driverButtons[i] = module.driver.buttons[i].get();

                    double[] driverAxes = new double[module.driver.axes.length];
                    for (int i = 0; i < driverAxes.length; i++) driverAxes[i] = module.driver.axes[i].get();

                    moments.add(new Moment(
                            module.outputs.driveLeftFront.get(),
                            module.outputs.driveLeftFront.get(),
                            module.inputs.driveLeft.getDistance(),
                            module.inputs.driveRight.getDistance(),
                            module.inputs.driveLeft.getRate(),
                            module.inputs.driveRight.getRate(),
                            driverButtons,
                            driverAxes
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

            module.inputs.driveLeft.reset();
            module.inputs.driveRight.reset();

            while (module.isEnabled() && tickCount < moments.size()) {

                if (System.nanoTime() - lastTickTime >= tickTiming) {

                    lastTickTime = System.nanoTime();

                    Moment moment = moments.get(tickCount);
                    Moment lastMoment = moments.get(tickCount > 0 ? tickCount : 0);

                    for (int i = 0; i < moment.driverButtons.length; i++) {

                        if (moment.driverButtons[i] && !lastMoment.driverButtons[i]) module.driver.buttons[i].triggerFalling();

                        if (!moment.driverButtons[i] && lastMoment.driverButtons[i]) module.driver.buttons[i].triggerRising();
                    }

                    //TODO fix conflict
                    //for (int i = 0; i < moment.driverAxes.length; i++)
                    //    if (moment.driverAxes[i] != lastMoment.driverAxes[i]) module.driver.axes[i].triggerChanged(moment.driverAxes[i]);

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

        private final boolean[] driverButtons;

        private final double[] driverAxes;

        private Moment(
                double leftDriveOutput,
                double rightDriveOutput,
                double leftEncoderPosition,
                double rightEncoderPosition,
                double leftEncoderVelocity,
                double rightEncoderVelocity,
                boolean[] driverButtons,
                double[] driverAxes
        ) {
            this.leftDriveOutput = leftDriveOutput;
            this.rightDriveOutput = rightDriveOutput;
            this.leftEncoderPosition = leftEncoderPosition;
            this.rightEncoderPosition = rightEncoderPosition;
            this.leftEncoderVelocity = leftEncoderVelocity;
            this.rightEncoderVelocity = rightEncoderVelocity;

            this.driverButtons = driverButtons;
            this.driverAxes = driverAxes;
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

    public void save(String name) {

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name)));

            for (Moment moment : moments) {

                writer.write(moment.leftDriveOutput + ",");
                writer.write(moment.rightDriveOutput + ",");
                writer.write(moment.leftEncoderPosition + ",");
                writer.write(moment.rightEncoderPosition + ",");
                writer.write(moment.leftEncoderVelocity + ",");
                writer.write(moment.rightEncoderVelocity + ",");

                for (double axis : moment.driverAxes) writer.write(axis + ",");

                for (boolean button : moment.driverButtons) writer.write(button + ",");

                writer.newLine();
            }

            writer.close();

        } catch (IOException e) { e.printStackTrace(); }
    }

    public void load(String name) {

        try {

            moments.clear();

            BufferedReader reader = new BufferedReader(new FileReader(new File(name)));

            for (String line = reader.readLine(); !line.equals(""); line = reader.readLine()) {

                String[] values = line.split(",");

                boolean[] buttons = new boolean[module.driver.buttons.length];
                double[] axes = new double[module.driver.axes.length];

                for (int i = 6; i < buttons.length; i++) buttons[i - 6] = Boolean.parseBoolean(values[i]);

                for (int i = 6 + buttons.length; i < axes.length; i++) axes[6 + buttons.length] = Double.parseDouble(values[i]);

                moments.add(new Moment(
                        Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3]),
                        Double.parseDouble(values[4]),
                        Double.parseDouble(values[5]),
                        buttons,
                        axes
                ));
            }

        } catch (IOException e) { e.printStackTrace(); }
    }
}
