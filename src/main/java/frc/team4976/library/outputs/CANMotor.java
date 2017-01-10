package frc.team4976.library.outputs;

import edu.wpi.first.wpilibj.CANTalon;
import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;

public class CANMotor extends Subsystem {

    private CANTalon motor;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    public CANMotor(IterativeRobotModule module, int port) {

        super(module);
        motor = new CANTalon(port);
    }

    public CANMotor(IterativeRobotModule module, int port, double ramp) {

        super(module);
        motor = new CANTalon(port);
        this.ramp = ramp;
    }

    private void update() {

        if (Math.abs(targetSpeed - speed) > ramp) {

            speed = targetSpeed > speed ? speed + ramp : speed - ramp;
            motor.set(speed);

        } else {

            speed = targetSpeed;
            motor.set(speed);
        }

        if (speed != targetSpeed) module.runNextLoop(this::update);
    }

    public void set(double speed) {

        if (targetSpeed == this.speed) module.runNextLoop(this::update);

        targetSpeed = speed;
    }

    public double get() { return speed; }
}
