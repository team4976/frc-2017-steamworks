package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Shooter {

    private NetworkTable table = NetworkTable.getTable("Shooter");

    private double targetRPM = table.getNumber("Setpoint (RPM)", 3100);
    private double targetError = table.getNumber("Target Error (RPM)", 100);

    public Shooter(Robot robot) {

        robot.addListener(new RobotStateListener() {

            @Override public void robotInit() {

                robot.outputs.shooter.disableControl();

                robot.runNextLoop(() -> {

                    //NetworkTable.getTable("Status").putNumber("Shooter Speed (RPM)", robot.outputs.shooter.getSpeed());
                    //NetworkTable.getTable("Status").putNumber("Shooter Error (RPM)", robot.outputs.shooter.getError());

                }, -1);
            }

            @Override public void disabledInit() {
                robot.outputs.shooter.set(0);
            }
        });

        robot.operator.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (!robot.outputs.shooter.isControlEnabled()) {

                    robot.outputs.shooter.enableControl();
                    robot.outputs.shooter.set(targetRPM);

                } else {

                    robot.outputs.shooter.set(0);
                    robot.outputs.shooter.disableControl();
                }
            }
        });


        robot.operator.B.addListener(new ButtonListener() { //TODO: Add targeting

            @Override public void pressed() {

                if (robot.outputs.shooter.getError() < targetError) {

                    System.out.println("<Shooter> Beginning to shoot.");

                    robot.elevator.run();

                    robot.runNextLoop(() -> { if (!robot.driver.RB.get()) robot.elevator.stop(); }, 500);
                }
            }

            @Override public void held() {

                if (robot.outputs.shooter.getError() < targetError)
                    System.out.println("<Shooter> Beginning to shoot.");

                Evaluable evaluable = new Evaluable() {

                    @Override public void eval() {

                        if (robot.outputs.shooter.getError() > targetError) {

                            if (robot.elevator.isRunning())
                                System.err.println("<Shooter> WARN: (RPM) too low to fire.");

                            robot.elevator.stop();

                        } else robot.elevator.run();

                        if (robot.driver.RB.get()) robot.runNextLoop(this);
                    }
                };

                evaluable.eval();
            }

            @Override public void falling() { robot.elevator.stop(); }
        });

        robot.operator.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.shooter.set(0);
                robot.outputs.shooter.disableControl();

                System.out.println("<Shooter> Stopping the shooter.");
            }
        });

        robot.operator.UP.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM += 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooter.get() != 0) robot.outputs.shooter.set(targetRPM);
            }
        });

        robot.operator.DOWN.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM -= 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooter.get() != 0) robot.outputs.shooter.set(targetRPM);
            }
        });

        robot.operator.LH.addListener((value -> robot.outputs.pivot.set(Math.abs(value) > 0.1 ? value * -0.2 : 0)));
    }
}