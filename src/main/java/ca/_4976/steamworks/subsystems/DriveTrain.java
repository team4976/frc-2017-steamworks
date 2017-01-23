package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.steamworks.Robot;

public class DriveTrain {

    private Robot module;

    private double[] velocity = new double[] {0.0, 0.0};

    public DriveTrain(Robot module) { this.module = module; }

    public void init() {

        module.driver.Y.addListener(new BooleanListener() {

            @Override public void rising() { module.outputs.shifter.output(true); }

            @Override public void falling() { module.outputs.shifter.output(false); }

        });

        module.driver.LH.addListener(value -> {

            System.out.println("speed is key");
            velocity[0] = value > 0 ? value * value : value * -value;
            drive();
        });

        module.driver.BT.addListener(value -> {

            velocity[1] = value;
            drive();
        });
    }

    private void drive() {

        module.outputs.driveLeftFront.set(velocity[0] + velocity[1]);
        module.outputs.driveLeftRear.set(velocity[0] + velocity[1]);
        module.outputs.driveRightFront.set(velocity[0] - velocity[1]);
        module.outputs.driveRightRear.set(velocity[0] - velocity[1]);
    }
}
