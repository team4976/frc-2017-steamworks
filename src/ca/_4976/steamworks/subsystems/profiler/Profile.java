package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;

public class Profile {

    public final double Shooter_RPM;
    public final double Hood_Position;
    public final double Turret_Position;

    public final Moment[] Moments;

    public final Evaluable[] Evaluable;
    public final int[] Evaluate_Timing;

    public Profile(
            double speed,
            double angle,
            double position,
            Moment[] moments,
            Evaluable[] evals,
            int[] times
    ) {
        Shooter_RPM = speed;
        Hood_Position = angle;
        Turret_Position = position;
        Moments = moments;
        Evaluable = evals;
        Evaluate_Timing = times;
    }
}
