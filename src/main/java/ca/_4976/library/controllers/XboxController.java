package ca._4976.library.controllers;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
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

    public Axis LH = new ThisAxis(0);
    public Axis LV = new ThisAxis(1);
    public Axis RH = new ThisAxis(4);
    public Axis RV = new ThisAxis(5);
    public Axis LT = new ThisAxis(2);
    public Axis RT = new ThisAxis(3);
    public Axis BT = new ThisAxis(3, 2);


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
            }

        }, -1);

        stick = new Joystick(port);
    }

    private class ThisButton extends Button {

        private ThisButton(int id) { super(id); }

        @Override public boolean get() { return stick.getRawButton(id); }
    }

    private class ThisAxis extends Axis {

        private ThisAxis(int a) { super(a); }

        private ThisAxis(int a, int b) { super(a, b); }

        @Override public double get() { return stick.getRawAxis(a) - (b > -1 ? stick.getRawAxis(b) : 0); }
    }
}
