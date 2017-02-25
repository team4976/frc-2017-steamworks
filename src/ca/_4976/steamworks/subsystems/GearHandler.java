package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

public class GearHandler {

    public GearHandler(Robot robot){

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                if (robot.outputs.gearRoller.getOutputCurrent() > 10)
                    robot.outputs.gearRoller.set(0);

                if (robot.outputs.gearRoller.get() != 0) robot.runNextLoop(this);
            }
        };

        robot.driver.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.gearRoller.set(0.5);
                robot.outputs.gearActuator.output(false);
            }
        });

        robot.driver.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.gearRoller.set(-0.5);
                robot.outputs.gearActuator.output(true);

                robot.runNextLoop(() -> robot.outputs.gearRoller.set(0), 300);
            }
        });
    }
}