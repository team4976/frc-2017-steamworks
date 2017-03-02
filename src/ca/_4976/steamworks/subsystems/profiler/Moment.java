package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;
import ca._4976.library.controllers.components.Double;

import java.util.Objects;
//import ca._4976.library.listeners.EvalType;

class Moment {

    final double leftDriveOutput;
    final double rightDriveOutput;

    final double leftEncoderPosition;
    final double rightEncoderPosition;

    final double leftEncoderVelocity;
    final double rightEncoderVelocity;

    Object[][] evaluables = null;
    Object[] states = null;

    Moment(
            double leftDriveOutput,
            double rightDriveOutput,
            double leftEncoderPosition,
            double rightEncoderPosition,
            double leftEncoderVelocity,
            double rightEncoderVelocity
    ) {
        this.leftDriveOutput = leftDriveOutput;
        this.rightDriveOutput = rightDriveOutput;
        this.leftEncoderPosition = leftEncoderPosition;
        this.rightEncoderPosition = rightEncoderPosition;
        this.leftEncoderVelocity = leftEncoderVelocity;
        this.rightEncoderVelocity = rightEncoderVelocity;

        this.evaluables = evaluables;
        this.states = states;
    }

    public void addControllerInputs(Object[][] evaluables, Object[] states) {

        this.evaluables = evaluables;

        this.states = states;
    }
}