package ca._4976.library.outputs;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class PWMMotor implements PIDOutput {

    private AsynchronousRobot module;

    private VictorSP motor;
    private int port;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    NetworkTable table = NetworkTable.getTable("PWMMotors");

    public PWMMotor(int port) {

        motor = new VictorSP(port);
        this.port = port;
    }

    public PWMMotor(AsynchronousRobot module, int port, double ramp) {

        this.module = module;
        motor = new VictorSP(port);
        this.ramp = ramp;
        this.port = port;
    }

    private void update() {

        if (Math.abs(targetSpeed - speed) > ramp) speed = targetSpeed > speed ? speed + ramp : speed - ramp;

        else speed = targetSpeed;

        motor.set(speed);
        table.putNumber(port + "", speed);

        if (speed != targetSpeed) module.runNextLoop(this::update);
    }

    public void set(double speed) {

        if (speed != targetSpeed) {

            targetSpeed = speed;
            update();

        } else targetSpeed = speed;
    }

    public double get() { return speed; }

    @Override public void pidWrite(double output) { motor.set(output); }
}
