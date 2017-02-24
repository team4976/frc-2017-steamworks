package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.Axis;
import ca._4976.library.controllers.Button;
import ca._4976.steamworks.Robot;

import java.util.ArrayList;

class Record implements Runnable {

    private Robot robot;

    private Config config = Config.getInstance();
    private Button[] buttons = new Button[0];
    private Axis[] axes = new Axis[0];

    Record(Robot robot) { this.robot = robot; }

    private Moment[] moments;

    @Override public void run() {

        ArrayList<Moment> moments = new ArrayList<>();

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        robot.inputs.driveLeft.reset();
        robot.inputs.driveRight.reset();

        while (robot.isEnabled()) {

            if (System.nanoTime() - lastTickTime >= config.tickTime) {

                lastTickTime = System.nanoTime();
                tickCount++;

                boolean[] driverButtons = new boolean[robot.driver.buttons.length];
                for (int i = 0; i < driverButtons.length; i++) driverButtons[i] = robot.driver.buttons[i].get();

                boolean[] operatorButtons = new boolean[robot.operator.buttons.length];
                for (int i = 0; i < operatorButtons.length; i++) operatorButtons[i] = robot.operator.buttons[i].get();

                double[] driverAxes = new double[robot.driver.axes.length];
                for (int i = 0; i < driverAxes.length; i++) driverAxes[i] = robot.driver.axes[i].get();

                moments.add(new Moment(
                        robot.outputs.driveLeftFront.get(),
                        robot.outputs.driveRightFront.get(),
                        robot.inputs.driveLeft.getDistance(),
                        robot.inputs.driveRight.getDistance(),
                        robot.inputs.driveLeft.getRate(),
                        robot.inputs.driveRight.getRate(),
                        null,
                        null
                ));

                avgTickRate += System.nanoTime() - lastTickTime;
            }
        }

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %%%.1f", config.tickTime / avgTickRate);
    }

    Moment[] getProfile() { return moments; }

    void changeControllerRecordPresets(Button[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Axis[] axes) { this.axes = axes; }
}