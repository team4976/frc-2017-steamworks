package ca._4976.steamworks.subsystems.vision.gear;

import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PID {

	Config config;
	Robot robot;

	private Forward forward = new Forward();
	private Turn turn = new Turn();
	private double[] motion = new double[] { 0.0, 0.0 };

	double x = 0;
	double y = 0;

	private PIDController forwardPID;
	private PIDController turnPID;

	PID(Robot robot, Config config) {

		this.robot = robot;
		this.config = config;

		forwardPID = new PIDController(
				config.forward.kP,
				config.forward.kI,
				config.forward.kD,
				forward,
				forward
		);

		turnPID = new PIDController(
				config.turn.kP,
				config.turn.kI,
				config.turn.kD,
				turn,
				turn
		);
	}

	void setPID(double tP, double tI, double tD, double mP, double mI, double mD) {

		turnPID.setPID(tP, tI, tD);
		forwardPID.setPID(mP, mI, mD);
	}

	void enable() {

		robot.drive.disableUserControl(true);

		turnPID.reset();
		turnPID.enable();

		forwardPID.reset();
		forwardPID.enable();
	}

	void disable() {

		robot.drive.arcadeDrive(0, 0);
		robot.drive.disableUserControl(false);

		turnPID.disable();
		forwardPID.disable();
	}

	public void setOffset(double offset) {

		turnPID.setSetpoint(config.resolution.width / 2 + offset);
	}

	double getError() { return (turnPID.getError() + forwardPID.getError()) / 2; }

	boolean isEnabled() { return turnPID.isEnabled() || forwardPID.isEnabled(); }

	private class Forward implements PIDOutput, PIDSource {

		@Override public void pidWrite(double output) {

			double maxOutput = 1 - Math.abs(turnPID.getError() / 20);
			if (maxOutput < 0) maxOutput = 0;

			forwardPID.setOutputRange(-maxOutput, maxOutput);

			motion[0] = -output;
			robot.drive.arcadeDrive(motion[0], motion[1]);

			System.out.printf("%.2f \t %.2f \t %.2f \t %.2f%n", motion[0], motion[1], x, y);
		}

		@Override public void setPIDSourceType(PIDSourceType pidSource) { }

		@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

		@Override public double pidGet() { return y; }
	}

	private class Turn implements PIDOutput, PIDSource {

		@Override public void pidWrite(double output) {

			motion[1] = -output;
			robot.drive.arcadeDrive(motion[0], motion[1]);
		}

		@Override public void setPIDSourceType(PIDSourceType pidSource) { }

		@Override public PIDSourceType getPIDSourceType() { return PIDSourceType.kDisplacement; }

		@Override public double pidGet() { return x; }
	}
}

