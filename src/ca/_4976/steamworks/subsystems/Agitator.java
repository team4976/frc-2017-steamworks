package ca._4976.steamworks.subsystems;

import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;

public class Agitator {

    private Robot robot;
    private Config.Agitator config;

    public Agitator(Robot robot) {

        this.robot = robot;
        config = robot.config.agitator;
    }

    void run() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.Current);
        robot.outputs.agitator.set(config.targetCurrent);
    }

    void stop() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.agitator.set(0);
    }

    void runReversed() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.agitator.set(-config.reverseSpeed);
    }

    void configNotify() {

        if (robot.outputs.agitator.getControlMode() == CANTalon.TalonControlMode.Current)
            robot.outputs.agitator.set(config.targetCurrent);

        else if (robot.outputs.agitator.get() != 0) robot.outputs.agitator.set(-config.reverseSpeed);
    }
}
