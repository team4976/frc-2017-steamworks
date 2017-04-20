package ca._4976.steamworks.subsystems.agitator;

import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;

public class Agitator {

    private Robot robot;
    private Config config = new Config();

    public Agitator(Robot robot) {

        this.robot = robot;

        config.setListener(() -> {

            if (robot.outputs.agitator.getControlMode() == CANTalon.TalonControlMode.Current)
                robot.outputs.agitator.set(config.targetCurrent);

            else if (robot.outputs.agitator.get() != 0) robot.outputs.agitator.set(-config.reverseSpeed);
        });
    }

    public void run() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.Current);
        robot.outputs.agitator.set(config.targetCurrent);
    }

    public void stop() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.agitator.set(0);
    }

    public void runReversed() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.agitator.set(-config.reverseSpeed);
    }
}
