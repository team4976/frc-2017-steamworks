package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

public class Winch {

    public Winch(Robot robot) {

        robot.operator.Y.addListener(new ButtonListener() {

            @Override public void falling() {

                robot.outputs.winchMaster.set(robot.outputs.winchMaster.get() == 0 ? -1 : 0);
            }
        });

        robot.operator.BACK.addListener(new ButtonListener() {
            @Override
            public void falling() {
                robot.outputs.winchMaster.set(robot.outputs.winchMaster.get() == 0 ? 0.1:  0);
            }
        });

        robot.operator.B.addListener(new ButtonListener() {

            @Override public void falling() {

                robot.outputs.arch.output(!robot.outputs.arch.isExtened());
            }
        });
    }
}
