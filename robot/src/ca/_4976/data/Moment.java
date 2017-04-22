package ca._4976.data;

public class Moment {

    public final double leftDriveOutput;
    public final double rightDriveOutput;
    public final double leftEncoderPosition;
    public final double rightEncoderPosition;
    public final double leftEncoderVelocity;
    public final double rightEncoderVelocity;

    public Moment(
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
}