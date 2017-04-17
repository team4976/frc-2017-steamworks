package ca._4976.steamworks.subsystems.vision;

import ca._4976.data.Contour;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;

public abstract class Tracker implements Runnable {

	protected UsbCamera camera = null;
	private CvSink cvSink = new CvSink("Rebel Vision");
	private Mat image = new Mat();
	private boolean run = false;

	protected ArrayList<MatOfPoint> output = new ArrayList<>();

	public boolean isRunning() { return run; }

	public synchronized void start() {

		run = true;
		new Thread(this).start();
	}

	public synchronized void stop() { run = false; }

	public void setCamera(String desc) {

		for (int i = 0; i < 4; i++) {

			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(i);

			if (camera.getDescription().toLowerCase().contains(desc.toLowerCase()))
				this.camera = camera;

			else camera.free();
		}
	}

	private void getFrame() {
		long frameTime = cvSink.grabFrame(image);
		if (frameTime == 0) {
			// There was an error, report it
			String error = cvSink.getError();
			DriverStation.reportError(error, true);
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

		while (run) { getFrame(); }
	}

	protected abstract void process(Mat image);

	protected abstract void process(Contour contour);
}
