package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class Agitator {

    private Robot robot;

    public Agitator(Robot robot) { this.robot = robot; }

    void run() { robot.outputs.agitator.set(-1); }

    void stop() { robot.outputs.agitator.set(0); }
}
