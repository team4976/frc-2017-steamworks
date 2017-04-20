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
	private double goalOffset = 0;
	private Robot robot;
	private CvSource erode;
	private CvSource dilate;
	private CvSource hsvThreshold;
	private CvSource findContours;
	private CvSource filterContours;
	private boolean correct = false;

	GoalTracker(Robot robot) {

		this.robot = robot;
		config = robot.config.goal;

		pid = new PIDController(
				config.kP,
				config.kI,
				config.kD,
				this,
				robot.outputs.pivot
		);
		pid.setSetpoint(config.resolution.width / 2 + config.offset);

		setCamera(Vision.getCamera("Microsoft"));

		setCameraSettings(
				config.resolution.asIntArray(),
				config.brightness,
				config.exposure,
				config.whiteBalance
		);

		if (Initialization.DEBUG) {

			erode = CameraServer.getInstance().putVideo("Goal-Erode", 160, 120);
			dilate = CameraServer.getInstance().putVideo("Goal-Dilate", 160, 120);
			hsvThreshold = CameraServer.getInstance().putVideo("Goal-HSV", 160, 120);
			findContours = CameraServer.getInstance().putVideo("Goal-Contours", 160, 120);
			filterContours = CameraServer.getInstance().putVideo("Goal-Filtered Contours", 160, 120);
		}
	}

	public synchronized void start() {

		run = true;
		new Thread(this).start();
	}

	@Override protected void process(Contour contour) {

		if (contour != null) {

			goalOffset = contour.position.center.x;

			if (correct) {

				double rpmCorrection = ((contour.position.center.y - 53) / 7) * 60;

				if (Math.abs(rpmCorrection) < 100 && robot.isAutonomous())
					robot.shooter.correctRPM(rpmCorrection);

				else robot.shooter.correctRPM(0);
			}

			if (!pid.isEnabled()) pid.enable();

		} else if (pid.isEnabled()) {

			pid.reset();
			pid.disable();
		}
	}

	@Override protected void process(Mat image) {

		System.out.println("hello");

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
			for (MatOfPoint matOfPoint : findContoursOutput) Operations.cvAdd(foundContours, matOfPoint, foundContours);
			findContours.putFrame(foundContours);

			Mat filteredContours = new Mat();
			for (MatOfPoint matOfPoint : output) Operations.cvAdd(filteredContours, matOfPoint, filteredContours);
			filterContours.putFrame(filteredContours);
		}
	}

	public void enableCorrectRPM(boolean enable) { correct = enable; }

	public double getError() { return pid.getError(); }

	@Override public void setPIDSourceType(PIDSourceType pidSource) { }

	@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

	@Override public double pidGet() { return goalOffset - (80 + config.offset); }

	public void configNotify() {

		setCameraSettings(
				config.resolution.asIntArray(),
				config.brightness,
				config.exposure,
				config.whiteBalance
		);

		pid.reset();
		pid.setPID(config.kP, config.kI, config.kD);
		pid.setSetpoint(config.resolution.width / 2 + config.offset);
	}
}
