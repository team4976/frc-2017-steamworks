package ca._4976.steamworks.subsystems.profiler;

import ca._4976.data.Moment;
import ca._4976.data.Profile;
import ca._4976.library.Evaluator;
import ca._4976.library.listeners.StringListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;
import com.ctre.CANTalon;

public class Playback implements Runnable {

	private StringListener listener = string -> {};
	private Profile profile = Profile.newEmpty();
	private boolean run = false;
	private boolean paused = false;
	private boolean disable = false;
	private Robot robot;
	private Config.Motion config;
	private double leftError = 0, rightError = 0;
	private double leftTarget = 0, rightTarget = 0;

	public Playback(Robot robot) {

		config = robot.config.motion;
		this.robot = robot;
	}

	public void setProfile(Profile profile) { this.profile = profile; }

	public void reset() {

		robot.inputs.driveLeft.reset();
		robot.inputs.driveRight.reset();
	}

	public synchronized void start( ) {

		run = true;
		new Thread(this).start();
	}

	public void stop() { run = false; }

	public void setPaused(boolean paused) { this.paused = paused; }

	public void setListener(StringListener listener) { this.listener = listener; }

	public void disableDrive() { disable = true; }

	public synchronized double getLeftTarget() { return leftTarget; }

	public synchronized double getRightTarget() { return rightTarget; }

	public synchronized double getLeftError() { return leftError; }

	public synchronized double getRightError() { return rightError; }

	@Override public void run() {

		long lastTick = System.nanoTime();
		long time = 0;

		StringBuilder builder = new StringBuilder();

		robot.shooter.setTargetRPM(profile.Shooter_RPM);
		robot.outputs.hood.set(profile.Hood_Position);
		robot.outputs.pivot.changeControlMode(CANTalon.TalonControlMode.Position);
		robot.outputs.pivot.set(profile.Turret_Position);

		if (profile.Run_Shooter) robot.shooter.run();

		if (profile.Extend_Winch_Arm) robot.outputs.arch.output(true);

		double leftIntegral = 0, rightIntegral = 0;
		double lastLeftError = 0, lastRightError = 0;

		for (int i = 0; robot.isEnabled() && run && i < profile.Moments.length;) {

			if (System.nanoTime() - lastTick >= config.tickTime && !paused) {

				lastTick = System.nanoTime();

				for (Evaluator evaluator : profile.Evaluators) {

					if (evaluator.delay == i)
						synchronized (this) { new Thread(evaluator.evaluable::eval); }
				}

				if (disable) {

					robot.outputs.driveLeftFront.set(0);
					robot.outputs.driveLeftRear.set(0);

					robot.outputs.driveRightFront.set(0);
					robot.outputs.driveRightRear.set(0);

				} else {

					Moment moment = profile.Moments[i];

					leftError = robot.inputs.driveLeft.getDistance() - moment.leftEncoderPosition;
					rightError = robot.inputs.driveRight.getDistance() - moment.rightEncoderPosition;

					leftTarget = moment.leftEncoderPosition;
					rightTarget = moment.rightEncoderPosition;

					leftIntegral += leftError * config.tickTime;
					rightIntegral += rightError * config.tickTime;

					double leftDerivative = (leftError - lastLeftError) / config.tickTime - moment.leftEncoderVelocity;
					double rightDerivative = (rightError - lastRightError) / config.tickTime - moment.rightEncoderVelocity;

					double leftDrive =
							moment.leftDriveOutput
									+ (config.kP * leftError)
									+ (config.kI * leftIntegral)
									+ (config.kD * leftDerivative);

					double rightDrive =
							moment.rightDriveOutput
									+ (config.kP * rightError)
									+ (config.kI * rightIntegral)
									+ (config.kD * rightDerivative);

					robot.outputs.driveLeftFront.set(leftDrive);
					robot.outputs.driveLeftRear.set(leftDrive);

					robot.outputs.driveRightFront.set(rightDrive);
					robot.outputs.driveRightRear.set(rightDrive);

					lastLeftError = leftError;
					lastRightError = rightError;

					builder.append(moment.leftDriveOutput);
					builder.append(',');
					builder.append(moment.rightDriveOutput);
					builder.append(',');
					builder.append(moment.leftEncoderPosition);
					builder.append(',');
					builder.append(moment.rightEncoderPosition);
					builder.append(',');
					builder.append(robot.inputs.driveLeft.getDistance());
					builder.append(',');
					builder.append(robot.inputs.driveRight.getDistance());
					builder.append(',');
					builder.append(leftError);
					builder.append(',');
					builder.append(rightError);
					builder.append(',');
				}

				listener.append(builder.toString());
				time += System.nanoTime() - lastTick;
				i++;
			}
		}

		robot.outputs.driveLeftFront.set(0);
		robot.outputs.driveLeftRear.set(0);
		robot.outputs.driveRightFront.set(0);
		robot.outputs.driveRightRear.set(0);

		time /= profile.Moments.length;
		System.out.printf("<Motion Control> Average tick time: %.3fms", time / 1e+6);
		System.out.printf(" %.1f%%%n", (time / config.tickTime) * 100);
	}
}