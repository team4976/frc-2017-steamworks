package ca._4976.steamworks.subsystems;

import ca._4976.steamworks.Robot;

public class Elevator {

    private Robot robot;

    public Elevator(Robot robot) { this.robot = robot; }

    void run() {

        robot.agitator.run();
        robot.outputs.elevator.set(-0.50);
    }

    void stop() {

        robot.outputs.elevator.set(0);
        robot.agitator.stop();
    }

    boolean isRunning() { return robot.outputs.elevator.get() != 0; }
}
