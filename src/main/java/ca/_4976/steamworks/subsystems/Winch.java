package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

/**
 * Created by User on 31/01/2017.
 */
public class Winch {

    boolean winchMotor = false;

    public Winch(Robot module) {

        module.driver.Y.addListener(new ButtonListener() {
            @Override
            public void rising() {
                winchMotor = !winchMotor;
                module.outputs.winchLeft.set(winchMotor ? 1 : 0);
                module.outputs.winchRight.set(winchMotor ? 1 : 0);
                //turns winch on if true off if false
                module.outputs.winchArm.output(true);
            }
        });
        module.inputs.winchSensor.addListener(new BooleanListener() {
            @Override
            public void changed() {
                module.outputs.winchLeft.set(0);
                module.outputs.winchRight.set(0);
            }
        });
    }
}
