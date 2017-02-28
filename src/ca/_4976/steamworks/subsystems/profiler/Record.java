package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;
import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.steamworks.Robot;

import java.util.ArrayList;

class Record implements Runnable {

    private Robot robot;

    private Config config = Config.getInstance();

    private Boolean[] buttons = new Boolean[0];
    private Double[] axes = new Double[0];

    Record(Robot robot) { this.robot = robot; }

    ArrayList<Moment> moments = new ArrayList<>();

    synchronized void addControllerInput(Object[] evaluables, Object[] states) {

        moments.get(moments.size() - 1).addControllerInputs(evaluables, states);
    }

    @Override public void run() {

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        robot.inputs.driveLeft.reset();
        robot.inputs.driveRight.reset();

        Evaluable evaluable = new Evaluable() {

            @Override public void eval() {

                ArrayList<Object[]> listeners = new ArrayList<>();
                ArrayList<Object> states = new ArrayList<>();

                for (Boolean button : buttons)
                    if (button.getState() != Boolean.EVAL_STATE.NON) {

                        listeners.add(button.getListeners());
                        states.add(button.getState());
                    }

                for (Double axis : axes)
                    if (axis.getState() != Double.EVAL_STATE.NON) {

                        listeners.add(axis.getListeners());
                        states.add(axis.getState());
                    }

                addControllerInput(listeners.toArray(), states.toArray());

                if (robot.isEnabled()) robot.runNextLoop(this);
            }
        };

        evaluable.eval();

        while (robot.isEnabled()) {

            if (System.nanoTime() - lastTickTime >= config.tickTime) {

                lastTickTime = System.nanoTime();
                tickCount++;

                robot.drive.update();

                moments.add(new Moment(
                        robot.outputs.driveLeftFront.get(),
                        robot.outputs.driveRightFront.get(),
                        robot.inputs.driveLeft.getDistance(),
                        robot.inputs.driveRight.getDistance(),
                        robot.inputs.driveLeft.getRate(),
                        robot.inputs.driveRight.getRate()
                ));

                avgTickRate += System.nanoTime() - lastTickTime;
            }
        }

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %%%.1f", config.tickTime / avgTickRate);
    }

    Moment[] getProfile() { return moments.toArray(new Moment[moments.size()]); }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}