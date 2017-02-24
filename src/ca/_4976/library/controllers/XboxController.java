package ca._4976.library.controllers;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

public class XboxController {

    private Joystick stick;

    public Button A = new ThisButton(1);
    public Button B = new ThisButton(2);
    public Button X = new ThisButton(3);
    public Button Y = new ThisButton(4);
    public Button LB = new ThisButton(5);
    public Button RB = new ThisButton(6);
    public Button BACK = new ThisButton(7);
    public Button START = new ThisButton(8);
    public Button LS = new ThisButton(9);
    public Button RS = new ThisButton(10);

    public Button UP = new POVButton(0);
    public Button UP_RIGHT = new POVButton(45);
    public Button RIGHT = new POVButton(90);
    public Button DOWN_RIGHT = new POVButton(135);
    public Button DOWN = new POVButton(180);
    public Button DOWN_LEFT = new POVButton(125);
    public Button LEFT = new POVButton(270);
    public Button UP_LEFT = new POVButton(315);

    public Button[] buttons = new Button[] { A, B, X, Y, LB, RB, BACK, START, LS, RS };

    public Axis LH = new ThisAxis(0);
    public Axis LV = new ThisAxis(1);
    public Axis RH = new ThisAxis(4);
    public Axis RV = new ThisAxis(5);
    public Axis LT = new ThisAxis(2);
    public Axis RT = new ThisAxis(3);
    public Axis BT = new ThisAxis(3, 2);

    public Axis[] axes = new Axis[] { LH, LV, RH, RV, LT, BT };

    public XboxController(AsynchronousRobot module, int port) {

        module.runNextLoop(() -> {

            if (module.isOperatorControl()) {

                A.eval();
                B.eval();
                X.eval();
                Y.eval();
                LB.eval();
                RB.eval();
                BACK.eval();
                START.eval();
                LS.eval();
                RS.eval();

                LV.eval();
                LH.eval();
                LT.eval();
                RT.eval();
                BT.eval();
                RV.eval();
                RH.eval();

                UP.eval();
                DOWN.eval();
            }

        }, -1);

        stick = new Joystick(port);
    }

    public void setRumble(double strength) {

        stick.setRumble(GenericHID.RumbleType.kLeftRumble, strength);
        stick.setRumble(GenericHID.RumbleType.kRightRumble, strength);
    }

    private class ThisButton extends Button {

        private ThisButton(int id) { super(id); }

        @Override public boolean get() { return stick.getRawButton(id); }
    }

    private class POVButton extends Button {

        private POVButton(int id) { super(id); }

        @Override public boolean get() { return stick.getPOV(0) == id; }
    }

    private class ThisAxis extends Axis {

        private ThisAxis(int a) { super(a); }

        private ThisAxis(int a, int b) { super(a, b); }

        @Override public double get() { return stick.getRawAxis(a) - (b > -1 ? stick.getRawAxis(b) : 0); }
    }
}
