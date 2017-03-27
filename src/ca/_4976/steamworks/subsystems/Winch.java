package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

public class Winch {

    public Winch(Robot robot) {

        robot.driver.Y.addListener(new ButtonListener() {

            @Override public void rising() { robot.outputs.winchMaster.set(-1); }

            @Override public void falling() { robot.outputs.winchMaster.set(-0.3); }
        });

        robot.driver.RB.addListener(new ButtonListener() {

            @Override public void rising() { robot.outputs.winchMaster.set(0.2); }

            @Override public void falling() { robot.outputs.winchMaster.set(0); }
        });

        robot.operator.Y.addListener(new ButtonListener() {

            @Override public void falling() { robot.outputs.arch.output(!robot.outputs.arch.isExtened());
            }
        });
    }
}
