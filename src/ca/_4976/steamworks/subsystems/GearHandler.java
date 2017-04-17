package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class GearHandler {

    private Config.GearHandler config;

    private int state = 0;

    public GearHandler(Robot robot){

        config = robot.config.gearHandler;

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() {

                state = 0;
                robot.outputs.roller.set(0);
            }
        });

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                System.out.println(config.currentLimit);

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

                robot.runNextLoop(() -> robot.outputs.roller.set(0), config.releaseTime);

                robot.runNextLoop(() -> { {

                    robot.outputs.gear.output(true);
                    state = 0;

                }}, config.raiseDelay);

                System.out.println("<Gear Handler> Releasing gear.");

                Double.parseDouble(" ");
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
}