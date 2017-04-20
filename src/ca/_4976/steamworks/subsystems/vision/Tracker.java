package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;

public abstract class Tracker implements Runnable {

	UsbCamera camera = null;
	
	private CvSink cvSink = new CvSink("Rebel Vision");
	private Mat image = new Mat();
	protected boolean run = false;

	protected ArrayList<MatOfPoint> output = new ArrayList<>();

	public boolean isRunning() { return run; }

	public synchronized void stop() { run = false; }

	protected void setCamera(UsbCamera camera) {

		this.camera = camera;
		cvSink.setSource(camera);
	}

	protected void setCameraSettings(
			int[] resolution,
	        int brightness,
	        int exposure,
	        int whiteBalance
	) {
		camera.setResolution(resolution[0], resolution[1]);
		camera.setBrightness(brightness);
		camera.setExposureManual(exposure);
		camera.setExposureHoldCurrent();
		camera.setWhiteBalanceManual(whiteBalance);
		camera.setWhiteBalanceHoldCurrent();
	}

	private void getFrame() {
		long frameTime = cvSink.grabFrame(image);
		if (frameTime == 0) {
			// There was an error, report it
			String error = cvSink.getError();
			DriverStation.reportError(error, true);
			System.out.println("hello");
		} else {
			// No errors, process the image
			process(image);

			if (output != null && !output.isEmpty()) process(Contour.getLargest(output));

			else process((Contour) null);
		}
	}

	@Override public void run() {

		if (Thread.currentThread().getId() == RobotBase.MAIN_THREAD_ID) {
			throw new IllegalStateException(
					"Vision.start() cannot be called from the main robot thread");
		}

		while (run) {

			getFrame();
		}
	}

	protected abstract void process(Mat image);

	protected abstract void process(Contour contour);
}
