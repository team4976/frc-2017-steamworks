package frc.team4976.library.outputs;

import edu.wpi.first.wpilibj.VictorSP;
import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;

public class PWMMotor extends Subsystem {

    private VictorSP motor;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    public PWMMotor(IterativeRobotModule module, int port) {

        super(module);
        motor = new VictorSP(port);
    }

    public PWMMotor(IterativeRobotModule module, int port, double ramp) {

        super(module);
        motor = new VictorSP(port);
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
