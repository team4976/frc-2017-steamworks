package ca._4976.steamworks.subsystems.vision;

import ca._4976.steamworks.Robot;

public class Vision {

	public GoalTracker goal;
	public GearTracker gear;

	public Vision(Robot robot) {

		goal = new GoalTracker(robot);
		gear = new GearTracker(robot);
	}
}
