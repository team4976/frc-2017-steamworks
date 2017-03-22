package ca._4976.steamworks.subsystems;

import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;

public class Agitator {

    private Robot robot;

    public Agitator(Robot robot) {

        this.robot = robot;
        robot.outputs.agitator.reverseOutput(true);
    }

    void run() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.Current);
        robot.outputs.agitator.set(1);
    }

    void stop() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.agitator.set(0);
    }

    void runReversed() {

        robot.outputs.agitator.changeControlMode(CANTalon.TalonControlMode.Current);
        robot.outputs.agitator.set(-1);
    }
}
