package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class GearHandler {

    private Robot robot;
    private Config config = new Config();

    private int state = 0;

    public GearHandler(Robot robot){

        this.robot = robot;

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() {

                state = 0;
                robot.outputs.roller.set(0);
            }
        });

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                if (robot.outputs.roller.getOutputCurrent() > config.currentLimit) {

                    state = 2;

                    robot.runNextLoop(() -> {

                        robot.outputs.roller.set(-config.gripSpeed);
                        robot.outputs.gear.output(true);
                        System.out.println("<Gear Handler> Gear roller over current perhaps we have a gear.");

                        robot.driver.setRumble(1);
                        robot.runNextLoop(() -> robot.driver.setRumble(0), 200);

                    }, config.gripDelay);
                }

                if (state == 1) robot.runNextLoop(this);
            }
        };

        robot.driver.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (state != 1) {

                    state = 1;

                    robot.outputs.roller.set(-config.intakeSpeed);
                    robot.outputs.gear.output(false);
                    robot.runNextLoop(currentControl, 5);

                    System.out.println("<Gear Handler> Attempting to intake a gear.");
                }
            }
        });

        robot.driver.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                state = 3;

                robot.outputs.roller.set(config.releaseSpeed);
                robot.outputs.gear.output(false);

                robot.runNextLoop(() -> { if (state == 3) {

                    robot.outputs.roller.set(0);

                }}, config.releaseTime);
                robot.runNextLoop(() -> { if (state == 3) {

                    robot.outputs.gear.output(true);
                    state = 0;

                }}, config.raiseDelay);

                System.out.println("<Gear Handler> Releasing gear.");
            }
        });

        robot.driver.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                state = 0;

                robot.outputs.roller.set(0);
                robot.outputs.gear.output(true);

                System.out.println("<Gear Handler> Resetting.");
            }
        });
    }

    private class Config {

        private NetworkTable table = NetworkTable.getTable("Gear Handler");

        private double intakeSpeed;
        private double releaseSpeed;
        private double gripSpeed;

        private int gripDelay;
        private int raiseDelay;

        private int releaseTime;

        private double currentLimit;

        private Config() {

            if (table.containsKey("Intake Speed (%)")) {

                intakeSpeed = table.getNumber("Intake Speed (%)", 0);

            } else {

                table.putNumber("Intake Speed (%)", 0);
                intakeSpeed = 0;
            }

            if (table.containsKey("Release Speed (%)")) {

                releaseSpeed = table.getNumber("Release Speed (%)", 0);

            } else {

                table.putNumber("Release Speed (%)", 0);
                releaseSpeed = 0;
            }

            if (table.containsKey("Grip Speed (%)")) {

                gripSpeed = table.getNumber("Grip Speed (%)", 0);

            } else {

                table.putNumber("Grip Speed (%)", 0);
                gripSpeed = 0;
            }

            if (table.containsKey("Grip Delay (MILLIS)")) {

                gripDelay = (int) table.getNumber("Grip Delay (MILLIS) (%)", 0);

            } else {

                table.putNumber("Grip Delay (MILLIS)", 0);
                gripDelay = 0;
            }

            if (table.containsKey("Raise Delay (MILLIS)")) {

                raiseDelay = (int) table.getNumber("Raise Delay (MILLIS) (%)", 0);

            } else {

                table.putNumber("Raise Delay (MILLIS)", 0);
                raiseDelay = 0;
            }

            if (table.containsKey("Release Time (MILLIS)")) {

                releaseTime = (int) table.getNumber("Release Time (MILLIS) (%)", 0);

            } else {

                table.putNumber("Release Time (MILLIS)", 0);
                releaseTime = 0;
            }

            if (table.containsKey("Current Limit (AMPS)")) {

                currentLimit = table.getNumber("Current Limit (AMPS)", 0);

            } else {

                table.putNumber("Current Limit (AMPS)", 0);
                currentLimit = 0;
            }

            table.addTableListener((source, key, value, isNew) -> {

                switch (key) {

                    case "Grip Speed (%)": gripSpeed = (double) value; break;
                    case "Release Speed (%)": releaseSpeed = (double) value; break;
                    case "Intake Speed (%)": intakeSpeed = (double) value; break;
                    case "Raise Delay (MILLIS)": raiseDelay = (int) (double) value; break;
                    case "Grip Delay (MILLIS)": gripDelay = (int) (double) value; break;
                    case "Release Time (MILLIS)": releaseTime = (int) (double) value; break;
                    case "Current Limit (AMPS)": currentLimit = (double) value; break;
                }
            });
        }
    }
}