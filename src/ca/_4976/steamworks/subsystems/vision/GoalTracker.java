package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import org.opencv.core.*;

import java.util.ArrayList;

public class GoalTracker extends Tracker implements PIDSource {

	private Config.Vision.Goal config;
	private Mat cvDilateOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<>();
	private PIDController pid;
	private double error = 0;
	private Robot robot;

	private CvSource erode;
	private CvSource dilate;
	private CvSource hsvThreshold;
	private CvSource findContours;
	private CvSource filterContours;

	GoalTracker(Robot robot) {

		this.robot = robot;

		setCamera("Logitech");
		config = robot.config.goal;

		pid = new PIDController(
				config.kP,
				config.kI,
				config.kD,
				this,
				robot.outputs.pivot
		);

		if (Initialization.DEBUG) {

			erode = CameraServer.getInstance().putVideo("Erode", 160, 120);
			dilate = CameraServer.getInstance().putVideo("Dilate", 160, 120);
			hsvThreshold = CameraServer.getInstance().putVideo("HSV", 160, 120);
			findContours = CameraServer.getInstance().putVideo("Contours", 160, 120);
			filterContours = CameraServer.getInstance().putVideo("Filtered Contours", 160, 120);
		}
	}

	@Override protected void process(Contour contour) {

		if (contour != null) {

			error = contour.position.center.x;

			double rpmCorrection = ((contour.position.center.y - 53) / 7) * 60;

			if (Math.abs(rpmCorrection) < 100 && robot.isAutonomous())
				robot.shooter.correctRPM(rpmCorrection);

			else robot.shooter.correctRPM(0);

			if (!pid.isEnabled()) pid.enable();

		} else if (pid.isEnabled()) {

			pid.reset();
			pid.disable();
		}
	}

	@Override protected void process(Mat image) {

		Operations.cvDilate(
				image,
				new Mat(),
				new Point(-1, -1),
				1.0,
				Core.BORDER_CONSTANT,
				new Scalar(-1),
				cvDilateOutput
		);

		Operations.cvErode(
				cvDilateOutput,
				new Mat(),
				new Point(-1, -1),
				1.0,
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

		Operations.findContours(hsvThresholdOutput, false, findContoursOutput);

		Operations.filterContours(
				findContoursOutput,
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

			dilate.putFrame(cvDilateOutput);
			erode.putFrame(cvErodeOutput);
			hsvThreshold.putFrame(hsvThresholdOutput);

			Mat foundContours = new Mat();

			findContoursOutput.forEach(matOfPoint -> Operations.cvAdd(foundContours, matOfPoint, foundContours));
			findContours.putFrame(foundContours);

			Mat filteredContours = new Mat();
			output.forEach(matOfPoint -> Operations.cvAdd(filteredContours, matOfPoint, filteredContours));
			filterContours.putFrame(filteredContours);
		}
	}

	@Override public void setPIDSourceType(PIDSourceType pidSource) { }

	@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

	@Override public double pidGet() { return error - (80 + config.offset); }

	public void configNotify() {

		camera.setResolution(config.resolution.width, config.resolution.height);

		pid.reset();
		pid.setPID(config.kP, config.kI, config.kD);
	}
}
