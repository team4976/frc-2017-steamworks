package ca._4976.library.outputs;

import ca._4976.library.AsynchronousRobot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class CANMotor implements PIDOutput {

    private AsynchronousRobot module;

    private CANTalon motor;
    private double ramp = 1;

    private double targetSpeed = 0;
    private double speed = 0;

    private NetworkTable table = NetworkTable.getTable("CANMotors");

    public CANMotor(int port){

        motor = new CANTalon(port);
    }

    public CANMotor(AsynchronousRobot module, int port, double ramp) {

        motor = new CANTalon(port);
        this.ramp = ramp;
    }

    private void update() {

        if (Math.abs(targetSpeed - speed) > ramp) speed = targetSpeed > speed ? speed + ramp : speed - ramp;

        else speed = targetSpeed;

        motor.set(speed);
        table.putNumber(motor.getDeviceID() + "", speed);

        if (speed != targetSpeed) module.runNextLoop(this::update);
    }

    public void set(double speed) {

        if (targetSpeed == this.speed) update();

        targetSpeed = speed;
    }

    public double get() { return speed; }

    @Override public void pidWrite(double output) { motor.set(output); }
}
