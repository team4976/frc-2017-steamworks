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
                module.outputs.door.output(!module.outputs.door.get());
                System.out.println(module.outputs.door.get() ? "Door opened" : "Door closed");
            }
        });

        module.driver.A.addListener(new ButtonListener() {
            @Override
            public void rising() {
                module.outputs.winchArm.output(!module.outputs.winchArm.get());
                System.out.println(module.outputs.winchArm.get() ? "Climber forward" : "Climber upright");
            }
        });

        module.inputs.gearSense.addListener(new BooleanListener() {
            @Override
            public void rising() {
                module.outputs.climb.output(true);
                System.out.println("Gear collected");
            }
        });
    }
}
