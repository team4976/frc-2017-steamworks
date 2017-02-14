package ca._4976.library.outputs;

import edu.wpi.first.wpilibj.Servo;

public class LinearActuator {

    private Servo servo;
    public Servo hood;

    public LinearActuator(int pin) {

        servo = new Servo(pin);
        hood = new Servo(1);
    }

    public void set(double length) { servo.set(length); }

    public double get() { return servo.get(); }
}
