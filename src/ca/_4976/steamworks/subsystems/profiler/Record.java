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

    synchronized void addControllerInput(ArrayList<Object[]> evaluables, ArrayList<Integer> ids, Object[] states) {

        Object[][] evals = new Object[evaluables.size()][];
        int[] iDs = new int[ids.size()];

        for (int i = 0; i < evals.length; i++) { evals[i] = evaluables.get(i); }

        for (int i = 0; i < iDs.length; i++) { iDs[i] = ids.get(i); }

        if (moments.size() > 0) moments.get(moments.size() - 1).addControllerInputs(evals, iDs, states, null);
    }

    @Override public void run() {

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        moments.clear();

        robot.inputs.driveLeft.reset();
        robot.inputs.driveRight.reset();

        Evaluable evaluable = new Evaluable() {

            @Override public void eval() {

                ArrayList<Object[]> listeners = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<Object> states = new ArrayList<>();

                for (int i = 0; i < axes.length; i++)
                    if (axes[i].getState() != Double.EVAL_STATE.NON) {

                        ids.add(i + 100);
                        listeners.add(axes[i].getListeners());
                        states.add(axes[i].getState());
                    }

                for (int i = 0; i < buttons.length; i++)
                    if (buttons[i].getState() != Boolean.EVAL_STATE.NON) {

                        ids.add(i);
                        listeners.add(buttons[i].getListeners());
                        states.add(buttons[i].getState());
                    }

                addControllerInput(listeners, ids, states.toArray());

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

        Moment[] staticMoments = new Moment[moments.size()];

        for (int i = 0; i < staticMoments.length; i++) staticMoments[i] = moments.get(i);

        new SaveFile().save(System.currentTimeMillis() / 1000 % 100000 + "", staticMoments);

        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %.1f%%%n", config.tickTime / avgTickRate);
    }

    Moment[] getProfile() {

        Moment[] profile = new Moment[moments.size()];

        for (int i = 0; i < moments.size(); i++) { profile[i] = moments.get(i); }

        return profile;
    }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}