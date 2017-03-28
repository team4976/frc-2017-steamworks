package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class Elevator {

    private Robot robot;
    private Config.Elevator config;

    public Elevator(Robot robot) {

        this.robot = robot;
        config = robot.config.elevator;

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { stop(); }
        });

        robot.operator.RB.addListener(new ButtonListener() {

            @Override public void falling() { stop(); }

            @Override public void rising() { runReversed(); }
        });
    }

    private void runReversed() {

        robot.agitator.runReversed();
        robot.outputs.elevator.set(-config.speed);
    }

    void run() {

        robot.agitator.run();
        System.out.println(config.speed);
        robot.outputs.elevator.set(config.speed);
    }

    void stop() {

        robot.outputs.elevator.set(0);
        robot.agitator.stop();
    }

    boolean isRunning() { return robot.outputs.elevator.get() != 0; }

    void configNotify() {

        if (robot.outputs.elevator.get() > 0) robot.outputs.elevator.set(config.speed);

        if (robot.outputs.elevator.get() < 0) robot.outputs.elevator.set(-config.speed);
    }
}
