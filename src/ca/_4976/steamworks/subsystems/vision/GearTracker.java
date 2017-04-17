package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import ca._4976.steamworks.Robot;
import org.opencv.core.Mat;

public class GearTracker extends Tracker {

	GearTracker(Robot robot) { setCamera("Logitech"); }

	@Override protected void process(Contour contour) {

	}

	@Override protected void process(Mat image) {

	}
}
