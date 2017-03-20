package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Elevator {

    private Robot robot;
    private Config config = new Config();

    public Elevator(Robot robot) {

        this.robot = robot;

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { stop(); }
        });
    }

    void run() {

        robot.agitator.run();
        robot.outputs.elevator.set(-config.speed);
    }

    void stop() {

        robot.outputs.elevator.set(0);
        robot.agitator.stop();
    }

    boolean isRunning() { return robot.outputs.elevator.get() != 0; }

    private class Config {

        private NetworkTable table = NetworkTable.getTable("Elevator");

        private double speed;

        private Config() {

            if (table.containsKey("Speed (%)")) {

                speed = table.getNumber("Speed (%)", 0);

            } else {

                table.putNumber("Speed (%)", 0);
                speed = 0;
            }

            table.addTableListener((source, key, value, isNew) -> {

                switch (key) {

                    case "Speed (%)": speed = (double) value; break;
                }

                if (isRunning()) robot.outputs.elevator.set(-speed);
            });
        }
    }
}
