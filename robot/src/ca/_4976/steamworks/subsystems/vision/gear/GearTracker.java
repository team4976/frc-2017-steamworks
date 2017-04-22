package ca._4976.steamworks.subsystems.vision.gear;

import ca._4976.data.Contour;
import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.vision.Operations;
import ca._4976.steamworks.subsystems.vision.Tracker;
import ca._4976.steamworks.subsystems.vision.Vision;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class GearTracker extends Tracker {

	private Config config = new Config();
	private Mat cvDilateOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<>();
	private ArrayList<MatOfPoint> convexHullsOutput = new ArrayList<>();
	private PID pid;
	private CvSource hsvThreshold;
	private CvSource contours;

	public GearTracker(Robot robot) {

		pid = new PID(robot, config);
		pid.setOffset(config.offset);

		config.setListener(() -> {

			setCameraSettings(
					config.resolution.asIntArray(),
					config.brightness,
					config.exposure,
					config.whiteBalance
			);

			pid.setOffset(config.offset);

			pid.setPID(
					config.turn.kP,
					config.turn.kI,
					config.turn.kD,
					config.forward.kP,
					config.forward.kI,
					config.forward.kD
			);
		});

		setCamera(Vision.getCamera("c903"));

		setCameraSettings(
				config.resolution.asIntArray(),
				config.brightness,
				config.exposure,
				config.whiteBalance
		);

		if (Initialization.DEBUG) {

			hsvThreshold = CameraServer.getInstance().putVideo("Gear-HSV", 160, 120);
			contours = CameraServer.getInstance().putVideo("Gear-Contours", 160, 120);
		}
	}

	public synchronized void start() {

		if (!run) {

			run = true;
			new Thread(this).start();
		}
	}

	@Override public synchronized void stop() {

		super.stop();
		pid.disable();
	}

	@Override protected void process(Contour contour) {

		if (contour != null) {

			pid.x = contour.position.center.x;
			pid.y = config.resolution.height - contour.position.center.y;

			if (!pid.isEnabled()) pid.enable();

		} else if (pid.isEnabled()) {

			System.out.println(":(");

			pid.disable();
		}
	}

	@Override protected void process(Mat image) {

		Operations.cvDilate(
				image,
				new Mat(),
				new Point(-1, -1),
				2.0,
				Core.BORDER_CONSTANT,
				new Scalar(-1),
				cvDilateOutput
		);

		Operations.cvErode(
				cvDilateOutput,
				new Mat(),
				new Point(-1, -1),
				2.0,
				Core.BORDER_CONSTANT,
				new Scalar(-1),
				cvErodeOutput
		);

		Operations.hsvThreshold(
				cvErodeOutput,
				config.hsvThresholdHue,
				config.hsvThresholdSaturation,
				config.hsvThresholdValue,
				hsvThresholdOutput
		);

		if (Initialization.DEBUG) hsvThreshold.putFrame(hsvThresholdOutput);

		Operations.findContours(hsvThresholdOutput, true, findContoursOutput);

		Operations.convexHulls(findContoursOutput, convexHullsOutput);

		Operations.filterContours(
				convexHullsOutput,
				config.filterContoursMinArea,
				config.filterContoursMinPerimeter,
				config.filterContoursMinWidth,
				config.filterContoursMaxWidth,
				config.filterContoursMinHeight,
				config.filterContoursMaxHeight,
				config.filterContoursSolidity,
				config.filterContoursMaxVertices,
				config.filterContoursMinVertices,
				config.filterContoursMinRatio,
				config.filterContoursMaxRatio,
				output);

		if (Initialization.DEBUG) {

			Imgproc.drawContours(hsvThresholdOutput, output, 0, new Scalar(255, 255));
			contours.putFrame(hsvThresholdOutput);
		}
	}

	public double getError() { return pid.getError(); }
}
