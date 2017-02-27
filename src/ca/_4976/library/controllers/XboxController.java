package ca._4976.library.controllers;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

public class XboxController {

    private Joystick stick;

    public Boolean A = new Button(1);
    public Boolean B = new Button(2);
    public Boolean X = new Button(3);
    public Boolean Y = new Button(4);
    public Boolean LB = new Button(5);
    public Boolean RB = new Button(6);
    public Boolean BACK = new Button(7);
    public Boolean START = new Button(8);
    public Boolean LS = new Button(9);
    public Boolean RS = new Button(10);

    public Boolean UP = new POV(0);
    public Boolean UP_RIGHT = new POV(45);
    public Boolean RIGHT = new POV(90);
    public Boolean DOWN_RIGHT = new POV(135);
    public Boolean DOWN = new POV(180);
    public Boolean DOWN_LEFT = new POV(125);
    public Boolean LEFT = new POV(270);
    public Boolean UP_LEFT = new POV(315);

    public Axis LH = new Axis(0);
    public Axis LV = new Axis(1);
    public Axis RH = new Axis(4);
    public Axis RV = new Axis(5);
    public Axis LT = new Axis(2);
    public Axis RT = new Axis(3);

    public XboxController(int port) { stick = new Joystick(port); }

    public void setRumble(double strength) {

        stick.setRumble(GenericHID.RumbleType.kLeftRumble, strength);
        stick.setRumble(GenericHID.RumbleType.kRightRumble, strength);
    }

    private class Button extends Boolean {

        private Button(int id) { super(id); }

        @Override public boolean get() { return stick.getRawButton(id); }
    }

    private class POV extends Boolean {

        private POV(int id) { super(id); }

        @Override public boolean get() { return stick.getPOV(0) == id; }
    }

    private class Axis extends Double {

        private Axis(int id) { super(id); }

        @Override public double get() { return stick.getRawAxis(id); }
    }
}
