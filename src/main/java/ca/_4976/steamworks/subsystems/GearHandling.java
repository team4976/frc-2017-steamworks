package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

public class GearHandling extends AsynchronousRobot{

    public GearHandling(Robot module){

        module.driver.B.addListener(new ButtonListener() {

            @Override

            public void rising() {
                module.outputs.gearDoor.output(!module.outputs.gearDoor.isExtened());
                System.out.print("<Gear Handling> The gear door was ");
                System.out.println(module.outputs.gearDoor.isExtened() ? "opened" : "closed" + ".");
            }

        });

        module.driver.A.addListener(new ButtonListener() {

            @Override

            public void rising() {
                module.outputs.winchArm.output(!module.outputs.winchArm.isExtened());
                System.out.print("<Gear Handling> The climber arm was ");
                System.out.println(module.outputs.winchArm.isExtened() ? "extended" : "retracted" + ".");
            }

        });

        module.inputs.gearSense.addListener(new BooleanListener() {

            @Override

            public void rising() {
                module.outputs.winchArm.output(true);
                System.out.print("<Gear Handling> The climber arm was extended.");
            }

        });
    }
}