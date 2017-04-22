package ca._4976.data;

import ca._4976.library.Evaluable;
import ca._4976.library.Evaluator;

public class Profile {

	public final double Shooter_RPM;
	public final double Hood_Position;
	public final double Turret_Position;
	public final boolean Run_Shooter;
	public final boolean Extend_Winch_Arm;
	public final double Disable_Motion;
	public final Moment[] Moments;
    public final Evaluator[] Evaluators;

    public Profile(
            double speed,
            double angle,
            double position,
            boolean runShooterAtStart,
            boolean extendWinchArmAtStart,
            double endTime,
            Moment[] moments,
            Evaluator[] evals
    ) {
        Shooter_RPM = speed;
        Hood_Position = angle;
        Turret_Position = position;
        Moments = moments;
        Evaluators = evals;
        Run_Shooter = runShooterAtStart;
        Extend_Winch_Arm = extendWinchArmAtStart;
        Disable_Motion = endTime;
    }

    public static Profile newEmpty() {

    	return new Profile(
    			0,
			    0,
			    0,
			    false,
			    false,
			    0,
			    new Moment[0],
			    new Evaluator[0]
	    );
    }

    public Profile getReversed() {

	    Moment[] moments = Moments;

	    double leftStart = moments[0].rightEncoderPosition;
	    double rightStart = moments[0].rightEncoderPosition;

	    for (int i = 0; i < moments.length; i++) {

		    moments[i] = new Moment(
				    -moments[i].leftDriveOutput,
				    -moments[i].rightDriveOutput,
				    leftStart - (moments[i].leftEncoderPosition - leftStart),
				    rightStart - (moments[i].rightEncoderPosition - rightStart),
				    -moments[i].leftEncoderVelocity,
				    -moments[i].rightEncoderVelocity
		    );
	    }

	    return new Profile(
			    Shooter_RPM,
			    Hood_Position,
			    Turret_Position,
			    Run_Shooter,
			    Extend_Winch_Arm,
			    Disable_Motion,
			    moments,
			    Evaluators
	    );
    }

    public Profile getMirrored() {

	    Moment[] moments = Moments;

	    for (int i = 0; i < moments.length; i++) {

		    moments[i] = new Moment(
				    -moments[i].rightDriveOutput,
				    -moments[i].leftDriveOutput,
				    -moments[i].rightEncoderPosition,
				    -moments[i].leftEncoderPosition,
				    -moments[i].rightEncoderVelocity,
				    -moments[i].leftEncoderVelocity
		    );
	    }

	    return new Profile(
			    Shooter_RPM,
			    Hood_Position,
			    Turret_Position,
			    Run_Shooter,
			    Extend_Winch_Arm,
			    Disable_Motion,
			    moments,
			    Evaluators
	    );
    }
}
