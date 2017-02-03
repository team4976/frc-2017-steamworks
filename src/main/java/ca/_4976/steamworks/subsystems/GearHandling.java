package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

/**
 * Created by mjam2 on 2017-01-31.
 */
public class GearHandling extends AsynchronousRobot{

    public boolean door = false;   //false=closed
    public boolean climber = false;    //false=upright
    public GearHandling(Robot module){

        //B button opens and closes the doors
        module.driver.B.addListener(new ButtonListener() {
            @Override
            public void pressed() {
                module.outputs.door.output(!door);
                door = !door;
            }
        });

        //A button changes the position of the climber so it is out or upright
        module.driver.A.addListener(new ButtonListener() {
            @Override
            public void pressed() {
                module.outputs.climb.output(!climber);
                climber = !climber;
            }
        });

        //gear sensing to close orifice when sensed
        module.inputs.gearSense.addListener(new BooleanListener() {
            @Override
            public void rising() {
                module.outputs.climb.output(true);
                climber = true;
            }
        });
    }
}
