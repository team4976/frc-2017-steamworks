package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.*;
import org.opencv.core.*;

import java.util.ArrayList;
import java.util.Arrays;

public class GearTracker extends Tracker {

	private Config.Vision.Gear config;
	private Mat cvDilateOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<>();
	private DrivePID pid;
	private Robot robot;
	private CvSource erode;
	private CvSource dilate;
	private CvSource hsvThreshold;
	private CvSource findContours;
	private CvSource filterContours;
	private double gearDistance = 0;
	private double gearOffset = 0;

	GearTracker(Robot robot) {

		this.robot = robot;
		config = robot.config.gear;

		pid = new DrivePID();

		setCamera(Vision.getCamera("c903"));

		setCameraSettings(
				config.resolution.asIntArray(),
				config.brightness,
				config.exposure,
				config.whiteBalance
		);

		if (Initialization.DEBUG) {

			erode = CameraServer.getInstance().putVideo("Gear-Erode", 160, 120);
			dilate = CameraServer.getInstance().putVideo("Gear-Dilate", 160, 120);
			hsvThreshold = CameraServer.getInstance().putVideo("Gear-HSV", 160, 120);
			findContours = CameraServer.getInstance().putVideo("Gear-Contours", 160, 120);
			filterContours = CameraServer.getInstance().putVideo("Gear-Filtered-Contours", 160, 120);
		}
	}

	public synchronized void start() {

		run = true;
		new Thread(this).start();
	}

	@Override protected void process(Contour contour) {

		if (contour != null) {

			gearOffset = contour.position.center.x;
			gearDistance = contour.position.center.y;

			if (!pid.isEnabled()) pid.enable();

		} else if (pid.isEnabled()) {

			pid.disable();
		}
	}

	@Override protected void process(Mat image) {

		System.out.println(Arrays.toString(config.hsvThresholdHue));

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

	public void configNotify() {

		setCameraSettings(
				config.resolution.asIntArray(),
				config.brightness,
				config.exposure,
				config.whiteBalance
		);
	}

	public double getError() { return pid.getError(); }

	private class DrivePID {

		private Forward forward = new Forward();
		private Turn turn = new Turn();
		private double[] motion = new double[] { 0.0, 0.0 };

		private PIDController forwardPID = new PIDController(
				config.forward.kP,
				config.forward.kI,
				config.forward.kD,
				forward,
				forward
		);

		private PIDController turnPID = new PIDController(
				config.turn.kP,
				config.turn.kI,
				config.turn.kD,
				turn,
				turn
		);

		private void enable() {

			turnPID.reset();
			turnPID.enable();

			forwardPID.reset();
			forwardPID.enable();
		}

		private void disable() {

			turnPID.disable();
			forwardPID.disable();
		}

		private double getError() { return (forwardPID.get() + turnPID.getError()) / 2; }

		private boolean isEnabled() { return turnPID.isEnabled() || forwardPID.isEnabled(); }

		private void drive() {

			robot.outputs.driveLeftFront.set(motion[1] + motion[0]);
			robot.outputs.driveLeftRear.set(motion[1] + motion[0]);
			robot.outputs.driveRightFront.set(motion[1] - motion[0]);
			robot.outputs.driveRightRear.set(motion[1] - motion[0]);
		}

		private class Forward implements PIDOutput, PIDSource {

			@Override public void pidWrite(double output) {

				double maxOutput = 1 - turnPID.getError() / 80;

				if (Math.abs(output) > maxOutput) output = output > 0 ? maxOutput : -maxOutput;

				motion[0] = output;
				drive();
			}

			@Override public void setPIDSourceType(PIDSourceType pidSource) { }

			@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

			@Override public double pidGet() { return gearDistance; }
		}

		private class Turn implements PIDOutput, PIDSource {

			@Override public void pidWrite(double output) {

				motion[1] = output;
				drive();
			}

			@Override public void setPIDSourceType(PIDSourceType pidSource) { }

			@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

			@Override public double pidGet() { return gearOffset; }
		}
	}
}
