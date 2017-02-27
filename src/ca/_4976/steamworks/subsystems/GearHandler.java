package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;

public class GearHandler {

    public GearHandler(Robot robot){

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { robot.outputs.gearRoller.set(0); }
        });

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                if (robot.outputs.gearRoller.getOutputCurrent() > 5) {

                    robot.outputs.gearRoller.set(0);
                    robot.outputs.gearActuator.output(true);
                    System.out.println("<Gear Handler> Gear roller over current perhaps we have a gear.");
                }

                if (robot.outputs.gearRoller.get() < 0) robot.runNextLoop(this);
            }
        };

        robot.driver.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.gearRoller.set(-0.5);
                robot.outputs.gearActuator.output(false);
                robot.runNextLoop(currentControl, 5);

                System.out.println("<Gear Handler> Attempting to intake a gear.");
            }
        });

        robot.driver.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.gearRoller.set(0.5);
                robot.outputs.gearActuator.output(false);

                robot.runNextLoop(() -> robot.outputs.gearRoller.set(0), 1000);

                System.out.println("<Gear Handler> Releasing gear.");
            }
        });
    }
}