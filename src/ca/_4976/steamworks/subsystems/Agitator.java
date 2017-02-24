package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class Agitator {

    public Agitator(Robot robot) {

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() {

                robot.outputs.agitator.set(0);
            }

            @Override public void autonomousInit() {

                robot.outputs.agitator.set(-1);
            }

            @Override public void teleopInit() {

                robot.outputs.agitator.set(-1);
            }
        });
    }
}
