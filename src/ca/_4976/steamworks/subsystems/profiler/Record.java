package ca._4976.steamworks.subsystems.profiler;

import ca._4976.data.Moment;
import ca._4976.data.Profile;
import ca._4976.library.Evaluator;
import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.DoubleListener;
import ca._4976.library.listeners.StringListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.Config;

import java.util.ArrayList;

public class Record implements Runnable {

	private StringListener listener = string -> { };
	private Profile profile = Profile.newEmpty();
	private Config.Motion config;
	private boolean run = false;
	private boolean paused = false;
	private Robot robot;
	private Boolean[] buttons = new Boolean[0];
	private Double[] axes = new Double[0];

	public Record(Robot robot) {

		this.robot = robot;
		config = robot.config.motion;
	}

	public void setListener(StringListener listener) { this.listener = listener; }

	public Profile getProfile() { return profile; }

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

	public boolean isPaused() { return paused; }

	public boolean isRunning() { return run; }

	public void setButtons(Boolean[] buttons) { this.buttons = buttons; }

	public void setAxes(Double[] axes) { this.axes = axes; }

	@Override public void run() {

		ArrayList<Moment> moments = new ArrayList<>();
		ArrayList<Evaluator> evaluators = new ArrayList<>();
		long lastTick = System.nanoTime();
		long time = 0;
		Boolean.EVAL_STATE[] buttonStates = new Boolean.EVAL_STATE[buttons.length];
		for (int x = 0; x < buttonStates.length; x++) buttonStates[x] = Boolean.EVAL_STATE.NON;
		Double.EVAL_STATE[] axesStates = new Double.EVAL_STATE[axes.length];
		for (int x = 0; x < axesStates.length; x++) axesStates[x] = Double.EVAL_STATE.NON;

		StringBuilder builder = new StringBuilder();

		double speed = robot.shooter.getTargetRPM();
		double angle = robot.outputs.hood.get();
		double position = robot.outputs.pivot.getPosition();

		if (config.runShooterAtStart) robot.shooter.run();

		if (config.extendWinchArmAtStart) robot.outputs.arch.output(true);

		for (int i = 0; robot.isEnabled() && run;) {

			if (System.nanoTime() - lastTick >= config.tickTime && !paused) {

				robot.drive.update();

				lastTick = System.nanoTime();

				Moment moment = new Moment(
						robot.outputs.driveLeftFront.get(),
						robot.outputs.driveRightFront.get(),
						robot.inputs.driveLeft.getDistance(),
						robot.inputs.driveRight.getDistance(),
						robot.inputs.driveLeft.getRate(),
						robot.inputs.driveRight.getRate()
				);

				moments.add(moment);

				builder.append(moment.leftDriveOutput);
				builder.append(',');
				builder.append(moment.rightDriveOutput);
				builder.append(',');
				builder.append(moment.leftEncoderPosition);
				builder.append(',');
				builder.append(moment.rightEncoderPosition);
				builder.append(',');
				builder.append(moment.leftEncoderVelocity);
				builder.append(',');
				builder.append(moment.rightEncoderVelocity);

				for (int x = 0; x < buttons.length; x++) {

					if (buttons[x].getState() != Boolean.EVAL_STATE.NON && buttons[x].getState() != buttonStates[x]) {

						ButtonListener[] listeners = buttons[x].getListeners();

						for (ButtonListener listener : listeners) {

							builder.append(',');
							builder.append(x);
							builder.append('.');
							builder.append(buttons[i].getState());

							switch (buttons[i].getState()) {

								case FALLING: evaluators.add(new Evaluator(listener::falling, i)); break;
								case RISING: evaluators.add(new Evaluator(listener::falling, i)); break;
								case PRESSED: evaluators.add(new Evaluator(listener::falling, i)); break;
								case HELD: evaluators.add(new Evaluator(listener::falling, i)); break;
							}
						}
					}

					buttonStates[x] = buttons[x].getState();
				}

				for (int x = 0; x < axes.length; x++) {

					if (axes[x].getState() != Double.EVAL_STATE.NON && axes[x].getState() != axesStates[x]) {

						DoubleListener[] listeners = axes[x].getListeners();

						for (DoubleListener listener : listeners) {

							double value = axes[x].get();

							builder.append(',');
							builder.append(x);
							builder.append(".CHANGED.");
							builder.append(value);

							evaluators.add(new Evaluator(() ->  listener.changed(value), i));
						}
					}

					axesStates[x] = axes[x].getState();
				}

				listener.append(builder.toString());

				time += System.nanoTime() - lastTick;
				i++;
			}
		}

		run = false;

		Moment[] f_Moments = new Moment[moments.size()];
		Evaluator[] f_Evaluators = new Evaluator[evaluators.size()];

		for (int i = 0; i < f_Moments.length; i++) f_Moments[i] = moments.get(i);
		for (int i = 0; i < f_Evaluators.length; i++) f_Evaluators[i] = evaluators.get(i);

		profile = new Profile(
				speed,
				angle,
				position,
				config.runShooterAtStart,
				config.extendWinchArmAtStart,
				0.0,
				f_Moments,
				f_Evaluators
		);

		time /= f_Moments.length;
		System.out.printf("<Motion Control> Average tick time: %.3fms", time / 1e+6);
		System.out.printf(" %.1f%%%n", (time / config.tickTime) * 100);
	}
}