package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.steamworks.Robot;

import java.util.Vector;

public class DriveTrain {

    private Robot module;

    private Vector<Double> velocity = new Vector<>(0, 0);

    public DriveTrain(Robot module) { this.module = module; }

    public void init() {

        module.driver.Y.addListener(new BooleanListener() {

            @Override public void rising() { module.outputs.shifter.output(true); }

            @Override public void falling() { module.outputs.shifter.output(false); }

        });

        module.driver.LH.addListener(value -> {

            velocity.set(0, value > 0 ? value * value : value * -value);
            drive();
        });

        module.driver.BT.addListener(value -> {

            velocity.set(1, value);
            drive();
        });
    }

    private void drive() {

        module.outputs.driveLeftFront.set(velocity.get(0) + velocity.get(1));
        module.outputs.driveLeftRear.set(velocity.get(0) + velocity.get(1));
        module.outputs.driveRightFront.set(velocity.get(0) - velocity.get(1));
        module.outputs.driveRightRear.set(velocity.get(0) - velocity.get(1));
    }
}
