package ca._4976.steamworks.subsystems.profiler;

class Moment {

    final double leftDriveOutput;
    final double rightDriveOutput;

    final double leftEncoderPosition;
    final double rightEncoderPosition;

    final double leftEncoderVelocity;
    final double rightEncoderVelocity;

    Object[][] evaluables = null;
    int[] ids = null;
    Object[] states = null;
    Object[] data = null;

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
    }

    void addControllerInputs(Object[][] evaluables, int[] ids, Object[] states, Object[] data) {

        this.evaluables = evaluables;
        this.ids = ids;
        this.states = states;
        this.data = data;
    }
}