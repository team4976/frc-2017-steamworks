package ca._4976.steamworks.subsystems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ca._4976.steamworks.Robot;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.vision.VisionPipeline;

import edu.wpi.first.wpilibj.vision.VisionThread;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.*;
import sun.nio.ch.Net;

public class VisionTracker implements VisionPipeline, PIDSource {

	private Robot robot;

	private Goal goal = null;

	private PIDController pidController;

	private NetworkTable table = NetworkTable.getTable("Vision");

	private boolean pause = false;

	private Config.Vision config;

	public VisionTracker(Robot robot) {

		this.robot = robot;
		config = robot.config.vision;

		UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture(0);
		UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(1);

		UsbCamera camera = (camera0.getDescription().toLowerCase().contains("lifecam") ? camera0 : camera1);

		camera.setResolution(160, 120);
		camera.setFPS(30);
		camera.setExposureManual(0);
		camera.setExposureHoldCurrent();
		camera.setWhiteBalanceManual(7000);
		camera.setWhiteBalanceHoldCurrent();

		VisionThread visionThread = new VisionThread(camera, this, VisionTracker::track);
		visionThread.start();

		table.putNumber("Setpoint", table.getNumber("Setpoint", 80));

		pidController = new PIDController(0.007, 0, 0.004, this, robot.outputs.pivot);
		pidController.setSetpoint(table.getNumber("Setpoint", 80));
	}

	public boolean isRunning() { return !pause; }


	public void halt() {

		robot.outputs.visionLight.set(false);
		pause = true;
	}

	public void run() {

		robot.outputs.visionLight.set(true);
		pidController.setSetpoint(table.getNumber("Setpoint", 80));
		pause = false;
	}

	public void track() {
		if (!pause && filterContoursOutput != null) {
			if (!filterContoursOutput.isEmpty()) {
				filterContoursOutput.sort(Comparator.comparingDouble(Imgproc::contourArea));
				goal = new Goal(filterContoursOutput.get(filterContoursOutput.size() - 1));
			} else {
				goal = null;
			}

			if (goal != null) {
				if (!pidController.isEnabled())
					pidController.enable();
			} else {
				if (pidController.isEnabled()) {
					pidController.reset();
					pidController.disable();
				}
			}
		} else {
			if (pidController.isEnabled()) {
				pidController.reset();
				pidController.disable();
			}
		}
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {

	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

	@Override
	public double pidGet() {
		if (goal != null) {

			return goal.centerX;

		} else
			return table.getNumber("Setpoint", 80);
	}

	double getError() { return pidController.getError(); }

	private class Goal {

		public int x, y, width, height, leftEdge, rightEdge, bottomEdge, topEdge;
		public double area, centerX, centerY;

		public Goal(MatOfPoint contour) {
			area = Imgproc.contourArea(contour);
			Rect rect = Imgproc.boundingRect(contour);

			x = rect.x;
			y = rect.y;
			width = rect.width;
			height = rect.height;

			leftEdge = x;
			rightEdge = x + width;
			bottomEdge = y;
			topEdge = y + height;
			topEdge = y + height;

			centerX = x + (width / 2);
			centerY = y + (height / 2);
		}
	}

	// OpenCV VisionTracker - Generated in GRIP

	private Mat cvDilateOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	@Override
	public void process(Mat source0) {
		if (!pause) {
			// Step CV_dilate0:

			Mat cvDilateSrc = source0;
			Mat cvDilateKernel = new Mat();
			Point cvDilateAnchor = new Point(-1, -1);
			double cvDilateIterations = 1.0;
			int cvDilateBordertype = Core.BORDER_CONSTANT;
			Scalar cvDilateBordervalue = new Scalar(-1);
			cvDilate(cvDilateSrc, cvDilateKernel, cvDilateAnchor, cvDilateIterations, cvDilateBordertype, cvDilateBordervalue, cvDilateOutput);

			// Step CV_erode0:
			Mat cvErodeSrc = cvDilateOutput;
			Mat cvErodeKernel = new Mat();
			Point cvErodeAnchor = new Point(-1, -1);
			double cvErodeIterations = 1.0;
			int cvErodeBordertype = Core.BORDER_CONSTANT;
			Scalar cvErodeBordervalue = new Scalar(-1);
			cvErode(cvErodeSrc, cvErodeKernel, cvErodeAnchor, cvErodeIterations, cvErodeBordertype, cvErodeBordervalue, cvErodeOutput);

			// Step HSV_Threshold0:
			Mat hsvThresholdInput = cvErodeOutput;
			double[] hsvThresholdHue = {60, 70};
			double[] hsvThresholdSaturation = {115, 255.0};
			double[] hsvThresholdValue = {40, 255.0};
			hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

			// Step Find_Contours0:
			Mat findContoursInput = hsvThresholdOutput;
			boolean findContoursExternalOnly = false;
			findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

			// Step Filter_Contours0:
			ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
			double filterContoursMinArea = 0.0;
			double filterContoursMinPerimeter = 30.0;
			double filterContoursMinWidth = 0.0;
			double filterContoursMaxWidth = 1000.0;
			double filterContoursMinHeight = 0.0;
			double filterContoursMaxHeight = 1000.0;
			double[] filterContoursSolidity = {0, 100};
			double filterContoursMaxVertices = 1000000.0;
			double filterContoursMinVertices = 0.0;
			double filterContoursMinRatio = 0.0;
			double filterContoursMaxRatio = 1000.0;
			filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
		}
	}

	private void cvDilate(Mat src, Mat kernel, Point anchor, double iterations, int borderType, Scalar borderValue, Mat dst) {
		if (kernel == null) {
			kernel = new Mat();
		}
		if (anchor == null) {
			anchor = new Point(-1, -1);
		}
		if (borderValue == null) {
			borderValue = new Scalar(-1);
		}
		Imgproc.dilate(src, dst, kernel, anchor, (int) iterations, borderType, borderValue);
	}

	private void cvErode(Mat src, Mat kernel, Point anchor, double iterations, int borderType, Scalar borderValue, Mat dst) {
		if (kernel == null) {
			kernel = new Mat();
		}
		if (anchor == null) {
			anchor = new Point(-1, -1);
		}
		if (borderValue == null) {
			borderValue = new Scalar(-1);
		}
		Imgproc.erode(src, dst, kernel, anchor, (int) iterations, borderType, borderValue);
	}

	private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val, Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
		Core.inRange(out, new Scalar(hue[0], sat[0], val[0]), new Scalar(hue[1], sat[1], val[1]), out);
	}

	private void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		} else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}

	private void filterContours(List<MatOfPoint> inputContours, double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int) hull.get(j, 0)[0];
				double[] point = new double[]{contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount) continue;
			final double ratio = bb.width / (double) bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}

	void configNotify() {

		pidController.setSetpoint(80 + config.offset);
	}
}