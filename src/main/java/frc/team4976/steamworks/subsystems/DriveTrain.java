package frc.team4976.steamworks.subsystems;

import frc.team4976.library.listeners.BooleanListener;
import frc.team4976.steamworks.RobotModule;
import jaci.openrio.toast.lib.math.Vec2D;

public class DriveTrain {

    private RobotModule module;

    private Vec2D velocity = new Vec2D(0, 0);

    public DriveTrain(RobotModule module) { this.module = module; }

    public void init() {

        module.driver.Y.addListener(new BooleanListener() {

            @Override public void rising() { module.outputs.shifter.output(true); }

            @Override public void falling() { module.outputs.shifter.output(false); }
        });

        module.driver.LH.addListener(value -> {

            velocity.setX(value > 0 ? value * value : value * -value);
            drive();
        });

        module.driver.BT.addListener(value -> {

            velocity.setY(value);
            drive();
        });
    }

    private void drive() {

        module.outputs.driveLeftFront.set(velocity.x() + velocity.y());
        module.outputs.driveLeftRear.set(velocity.x() + velocity.y());
        module.outputs.driveRightFront.set(velocity.x() - velocity.y());
        module.outputs.driveRightRear.set(velocity.x() - velocity.y());
    }
}
