package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

public class Winch {

    public Winch(Robot module) {

        module.operator.Y.addListener(new ButtonListener() {

            @Override public void pressed() {

                module.outputs.winchMaster.set(module.outputs.winchMaster.get() == 0 ? -1 : 0);
            }
        });

        module.operator.BACK.addListener(new ButtonListener() {
            @Override
            public void pressed() {
                module.outputs.winchMaster.set(module.outputs.winchMaster.get() == 0 ? 1:  0);
            }
        });



        module.operator.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                module.outputs.winchArm.output(!module.outputs.winchArm.isExtened());
            }
        });

        module.inputs.winchSensor.addListener(new BooleanListener() {
            @Override
            public void changed() {
                module.outputs.winchMaster.set(0);
            }
        });
    }
}
