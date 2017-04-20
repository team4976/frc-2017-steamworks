package ca._4976.steamworks.subsystems.elevator;

import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;

public class Elevator {

    private Robot robot;
    private Config config = new Config();

    public Elevator(Robot robot) {

        this.robot = robot;

        config.setListener(() -> {

            if (robot.outputs.elevator.get() > 0) robot.outputs.elevator.set(config.speed);

            if (robot.outputs.elevator.get() < 0) robot.outputs.elevator.set(-config.speed);
        });

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() { stop(); }
        });

        robot.operator.RB.addListener(new ButtonListener() {

            @Override public void falling() { stop(); }

            @Override public void rising() { runReversed(); }
        });
    }

    private void runReversed() {

        robot.agitator.runReversed();
        robot.outputs.elevator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.elevator.set(-0.5);
    }

    public void run() {

        robot.agitator.run();
        robot.outputs.elevator.changeControlMode(CANTalon.TalonControlMode.Speed);
        robot.outputs.elevator.set(config.speed);
    }

    public void stop() {

        robot.outputs.elevator.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        robot.outputs.elevator.set(0);
        robot.agitator.stop();
    }

    public boolean isRunning() { return robot.outputs.elevator.get() != 0; }
}
