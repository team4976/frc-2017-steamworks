package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Elevator {

    private Robot robot;

    public Elevator(Robot robot) {

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { stop(); }
        });

        NetworkTable.getTable("Elevator").putNumber("speed", NetworkTable.getTable("Elevator").getNumber("speed", 0));
        this.robot = robot;
    }

    void run() {

        robot.agitator.run();
        robot.outputs.elevator.set(-NetworkTable.getTable("Elevator").getNumber("speed", 0));
    }

    void stop() {

        robot.outputs.elevator.set(0);
        robot.agitator.stop();
    }

    boolean isRunning() { return robot.outputs.elevator.get() != 0; }
}
