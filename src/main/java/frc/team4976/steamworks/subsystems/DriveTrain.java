package frc.team4976.steamworks.subsystems;

import frc.team4976.steamworks.RobotModule;
import jaci.openrio.toast.lib.math.Vec2D;

public class DriveTrain {

    private RobotModule module;

    private Vec2D velocity = new Vec2D(0, 0);

    public DriveTrain(RobotModule module) { this.module = module; }

    public void init() {

        module.driver.LH.addListener(value -> {

            velocity.setX(value > 0 ? value * value : value * -value);

            System.out.println("Stick: " + value);
            drive();
        });

        module.driver.BT.addListener(value -> {

            System.out.println("Trigger: " + value);
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
