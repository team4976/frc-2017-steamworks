package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class Winch {

	private Robot robot;
    private Config.Winch config;

    public Winch(Robot robot) {

    	this.robot = robot;
        config = robot.config.winch;

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { robot.outputs.winchMaster.set(0); }
        });

        robot.driver.Y.addListener(new ButtonListener() {

            @Override public void rising() { robot.outputs.winchMaster.set(-1); }

            @Override public void falling() { robot.outputs.winchMaster.set(-config.holdSpeed); }
        });

        robot.driver.RB.addListener(new ButtonListener() {

            @Override public void rising() { robot.outputs.winchMaster.set(0.5); }

            @Override public void falling() { robot.outputs.winchMaster.set(0); }
        });

        robot.operator.Y.addListener(new ButtonListener() {

        	@Override public void falling() { robot.outputs.arch.output(!robot.outputs.arch.isExtended());
            }
        });
    }

    public void extend() { robot.outputs.arch.output(false); }
}
