package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;

public class Profile {

	final double Shooter_RPM;
	final double Hood_Position;
	final double Turret_Position;

	final boolean Run_Shooter;
	final boolean Extend_Winch_Arm;

	final Moment[] Moments;

	public final double Disable_Motion;

    public final Evaluable[] Evaluable;
    final int[] Evaluate_Timing;

    Profile(
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
