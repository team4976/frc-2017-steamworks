package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import ca._4976.library.Initialization;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class GearTracker extends Tracker {

	private Config.Vision.Gear config;
	private Mat cvDilateOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<>();
	private ArrayList<MatOfPoint> convexHullsOutput = new ArrayList<>();
	private DrivePID pid;
	private Robot robot;
	private CvSource hsvThreshold;
	private CvSource contours;
	private double gearDistance = 0;
	private double gearOffset = 0;

	GearTracker(Robot robot) {

		this.robot = robot;
		config = robot.config.gear;

		pid = new DrivePID();
		pid.setOffset(config.offset);

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

		run = true;
		new Thread(this).start();
	}

	@Override protected void process(Contour contour) {


		if (contour != null) {

			gearOffset = contour.position.center.x;
			gearDistance = config.resolution.height - contour.position.center.y;

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

	public void configNotify() {

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

		private void setPID(double tP, double tI, double tD, double mP, double mI, double mD) {

			turnPID.setPID(tP, tI, tD);
			forwardPID.setPID(mP, mI, mD);
		}

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

		public void setOffset(double offset) {

			turnPID.setSetpoint(config.resolution.width / 2 + offset);
		}

		private double getError() { return turnPID.getError(); }

		private boolean isEnabled() { return turnPID.isEnabled() || forwardPID.isEnabled(); }

		private void drive() {

			robot.outputs.driveLeftFront.set(motion[1] + motion[0]);
			robot.outputs.driveLeftRear.set(motion[1] + motion[0]);
			robot.outputs.driveRightFront.set(motion[1] - motion[0]);
			robot.outputs.driveRightRear.set(motion[1] - motion[0]);
		}

		private class Forward implements PIDOutput, PIDSource {

			@Override public void pidWrite(double output) {

				double maxOutput = 1 - Math.abs(turnPID.getError() / 20);
				if (maxOutput < 0) maxOutput = 0;

				maxOutput = 1;

				if (Math.abs(output) > maxOutput) output = output > 0 ? maxOutput : -maxOutput;

				motion[0] = -output;
				drive();

				System.out.printf("%.2f\t%.2f\t%.2f%n",motion[0], gearOffset, gearDistance);
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
