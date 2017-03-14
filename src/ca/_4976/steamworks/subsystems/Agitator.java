package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.Initialization;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class Agitator {

    private Robot robot;

    boolean isRunning = false;

    public Agitator(Robot robot) {

        Evaluable evaluable = new Evaluable() {
            @Override
            public void eval() {

                if (isRunning && robot.outputs.roller.getOutputCurrent() > 50 && robot.outputs.roller.get() < 0) {

                    System.err.println("<Agitator> WARN: Unjamming");

                    robot.runNextLoop(() -> robot.outputs.roller.set(.5));
                    robot.runNextLoop(() -> robot.outputs.roller.set(-.5), 300);

                } else if (!isRunning) robot.outputs.roller.set(0);
            }
        };

        Initialization.HARDWARE_INPUT_EVALS.add(evaluable);

        this.robot = robot;
    }

    void run() {

        if (robot.outputs.roller.get() == 0) {

            isRunning = true;
            robot.outputs.roller.set(-.5);
        }
    }

    void stop() {

        robot.outputs.roller.set(0);
        isRunning = false;
    }
}
