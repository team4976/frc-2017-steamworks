package ca._4976.data;

import ca._4976.library.Evaluable;

public class Profile {

	public final double Shooter_RPM;
	public final double Hood_Position;
	public final double Turret_Position;
	public final boolean Run_Shooter;
	public final boolean Extend_Winch_Arm;
	public final Moment[] Moments;
	public final double Disable_Motion;
    public final Evaluable[] Evaluable;
    public final int[] Evaluate_Timing;

    public Profile(
            double speed,
            double angle,
            double position,
            Moment[] moments,
            Evaluable[] evals,
            int[] times,
            boolean runShooterAtStart,
            boolean extendWinchArmAtStart,
            double endTime
    ) {
        Shooter_RPM = speed;
        Hood_Position = angle;
        Turret_Position = position;
        Moments = moments;
        Evaluable = evals;
        Evaluate_Timing = times;
        Run_Shooter = runShooterAtStart;
        Extend_Winch_Arm = extendWinchArmAtStart;
        Disable_Motion = endTime;
    }
}
