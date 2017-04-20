package ca._4976.steamworks.subsystems.vision;

import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.vision.gear.GearTracker;
import ca._4976.steamworks.subsystems.vision.goal.GoalTracker;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class Vision {

	public GoalTracker goal;
	public GearTracker gear;

	public Vision(Robot robot) {

		goal = new GoalTracker(robot);
		gear = new GearTracker(robot);

		robot.addListener(new RobotStateListener() {

			@Override public void disabledInit() {

				goal.stop();
				gear.stop();
			}
		});
	}

	private static UsbCamera[] cameras = new UsbCamera[1];

	public static UsbCamera getCamera(String desc) {

		for (int i = 0; i < cameras.length; i++) {

			if (cameras[i] == null) cameras[i] = CameraServer.getInstance().startAutomaticCapture(i);

			System.out.println(cameras[i].getDescription());

			if (cameras[i].getDescription().toLowerCase().contains(desc.toLowerCase()))
				return cameras[i];
		}

		return cameras[0];
	}
}
