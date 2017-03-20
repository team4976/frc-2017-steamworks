package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class GearHandler {

    private boolean isTryingToIntakeGear = false;

    public GearHandler(Robot robot){

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() {

                isTryingToIntakeGear = false;
                robot.outputs.roller.set(0);
            }
        });

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                if (robot.outputs.roller.getOutputCurrent() > 5) {

                    robot.runNextLoop(() -> {

                        isTryingToIntakeGear = false;

                        robot.outputs.roller.set(-0.1);
                        robot.outputs.gear.output(true);
                        System.out.println("<Gear Handler> Gear roller over current perhaps we have a gear.");

                        robot.driver.setRumble(1);
                        robot.runNextLoop(() -> robot.driver.setRumble(0), 200);

                    }, 500);
                }

                if (robot.outputs.roller.get() < 0) robot.runNextLoop(this);
            }
        };

        robot.driver.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (!isTryingToIntakeGear) {

                    isTryingToIntakeGear = true;

                    robot.outputs.roller.set(-0.5);
                    robot.outputs.gear.output(false);
                    robot.runNextLoop(currentControl, 5);

                    System.out.println("<Gear Handler> Attempting to intake a gear.");
                }
            }
        });

        robot.driver.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                isTryingToIntakeGear = false;

                robot.outputs.roller.set(0.5);
                robot.outputs.gear.output(false);

                robot.runNextLoop(() -> robot.outputs.roller.set(0), 1000);
                robot.runNextLoop(() -> robot.outputs.gear.output(true), 1500);

                System.out.println("<Gear Handler> Releasing gear.");
            }
        });

        robot.driver.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                isTryingToIntakeGear = false;

                robot.outputs.roller.set(0);
                robot.outputs.gear.output(true);

                System.out.println("<Gear Handler> Raising gear.");
            }
        });
    }
}