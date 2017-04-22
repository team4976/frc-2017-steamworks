package ca._4976.library.outputs;

import edu.wpi.first.wpilibj.Servo;

public class LinearActuator {

    private Servo servo;

    public LinearActuator(int pin) {

        servo = new Servo(pin);
    }

    public void set(double length) { servo.set(length); }

    public double get() { return servo.get(); }
}
