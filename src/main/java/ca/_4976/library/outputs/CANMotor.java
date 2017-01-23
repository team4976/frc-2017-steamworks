package ca._4976.library.outputs;

import ca._4976.library.IterativeRobot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.PIDOutput;

public class CANMotor implements PIDOutput {

    private IterativeRobot module;

    private CANTalon motor;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    public CANMotor(int port){

        motor = new CANTalon(port);
    }

    public CANMotor(IterativeRobot module, int port, double ramp) {

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

        if (targetSpeed == this.speed) update();

        targetSpeed = speed;
    }

    public double get() { return speed; }

    @Override public void pidWrite(double output) { motor.set(output); }
}
