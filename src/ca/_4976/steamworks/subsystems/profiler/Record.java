package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;
import ca._4976.library.controllers.XboxController;
import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.DoubleListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import javafx.scene.input.DataFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Record implements Runnable {

    private Robot robot;

    private Config.Motion config;

    private Boolean[] buttons = new Boolean[0];
    private Double[] axes = new Double[0];

    Record(Robot robot) {

        this.robot = robot;
        config = robot.config.motion;
    }

    private Profile profile = null;

    private ArrayList<Moment> moments = new ArrayList<>();
    private ArrayList<Evaluable> evaluables = new ArrayList<>();
    private ArrayList<Long> times = new ArrayList<>();

    private String append = "";
    private long start;

    @Override public void run() {

        System.out.println("Starting Recording.");

        start = System.currentTimeMillis();
        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        moments.clear();

        robot.inputs.driveLeft.reset();
        robot.inputs.driveRight.reset();

        robot.runNextLoop(new Evaluable() {

            @Override public void eval() {

                for (int i = 0; i < buttons.length; i++) {

                    if (buttons[i].getState() != Boolean.EVAL_STATE.NON) {

                        ButtonListener[] listeners = buttons[i].getListeners();

                        for (ButtonListener listener : listeners) {

                            switch (buttons[i].getState()) {

                                case FALLING:
                                    append += "," + i + ".FALLING";
                                    evaluables.add(listener::falling);
                                    break;
                                case RISING:
                                    append += "," + i + ".RISING";
                                    evaluables.add(listener::rising);
                                    break;
                                case PRESSED:
                                    append += "," + i + ".PRESSED";
                                    evaluables.add(listener::pressed);
                                    break;
                                case HELD:
                                    append += "," + i + ".HELD";
                                    evaluables.add(listener::held);
                                    break;
                            }

                            times.add(System.currentTimeMillis() - start);
                        }
                    }
                }

                for (int i = 0; i < axes.length; i++) {

                    if (axes[i].getState() != Double.EVAL_STATE.NON) {

                        DoubleListener[] listeners = axes[i].getListeners();

                        for (DoubleListener listener : listeners) {

                            switch (axes[i].getState()) {

                                case CHANGED:
                                    double value = axes[i].get();
                                    append += "," + i + ".CHANGED." + value;
                                    evaluables.add(() -> listener.changed(value));
                                    break;
                            }

                            times.add(System.currentTimeMillis() - start);
                        }
                    }
                }

                if (robot.isEnabled()) robot.runNextLoop(this);
            }
        });

        double speed = robot.shooter.getTargetRPM();
        double angle = robot.outputs.hood.get();
        double position = robot.outputs.pivot.getPosition();

        if (config.runShooterAtStart) robot.shooter.run();

        if (config.extendWinchArmAtStart) robot.outputs.arch.output(true);

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();

            String file = "/home/lvuser/motion/Recording " + dateFormat.format(date) + ".csv";

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));

            writer.write("config:" + speed + "," + angle + "," + position + ",true,true");
            writer.newLine();

            while (robot.isEnabled() && !robot.driver.BACK.get()) {

                if (System.nanoTime() - lastTickTime >= config.tickTime) {

                    lastTickTime = System.nanoTime();
                    tickCount++;

                    robot.drive.update();

                    Moment moment = new Moment(
                            robot.outputs.driveLeftFront.get(),
                            robot.outputs.driveRightFront.get(),
                            robot.inputs.driveLeft.getDistance(),
                            robot.inputs.driveRight.getDistance(),
                            robot.inputs.driveLeft.getRate(),
                            robot.inputs.driveRight.getRate()
                    );

                    writer.write(moment.leftDriveOutput + ",");
                    writer.write(moment.rightDriveOutput + ",");
                    writer.write(moment.leftEncoderPosition + ",");
                    writer.write(moment.rightEncoderPosition + ",");
                    writer.write(moment.leftEncoderVelocity + ",");
                    writer.write(moment.rightEncoderVelocity + append);

                    append = "";

                    writer.newLine();
                    writer.flush();

                    moments.add(moment);

                    avgTickRate += System.nanoTime() - lastTickTime;
                }
            }

            writer.close();

        } catch (IOException e) { e.printStackTrace(); }

        Moment[] staticMoments = new Moment[moments.size()];
        Evaluable[] staticEvals = new Evaluable[evaluables.size()];
        int[] staticTimes = new int[times.size()];

        for (int i = 0; i < staticMoments.length; i++) staticMoments[i] = moments.get(i);
        for (int i = 0; i < staticEvals.length; i++) staticEvals[i] = evaluables.get(i);
        for (int i = 0; i < staticTimes.length; i++) staticTimes[i] = times.get(i).intValue();

        profile = new Profile(speed, angle, position, staticMoments,
                staticEvals, staticTimes, config.runShooterAtStart, config.extendWinchArmAtStart, 0.0);

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3fms", avgTickRate / 1e+6);
        System.out.printf(" %.1f%%%n", (avgTickRate / config.tickTime) * 100);
    }

    Profile getProfile() { return profile;  }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}