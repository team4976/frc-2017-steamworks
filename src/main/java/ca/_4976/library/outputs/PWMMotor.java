package ca._4976.library.outputs;

import ca._4976.library.IterativeRobot;
import edu.wpi.first.wpilibj.VictorSP;

public class PWMMotor {

    private IterativeRobot module;

    private VictorSP motor;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    public PWMMotor(int port) {

        motor = new VictorSP(port);
    }

    public PWMMotor(IterativeRobot module, int port, double ramp) {

        this.module = module;
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
