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

                robot.outputs.shooterMaster.disableControl();

                robot.runNextLoop(() -> {

                    //NetworkTable.getTable("Status").putNumber("Shooter Speed (RPM)", robot.outputs.shooterMaster.getSpeed());
                    //NetworkTable.getTable("Status").putNumber("Shooter Error (RPM)", robot.outputs.shooterMaster.getError());

                }, -1);
            }

            @Override public void disabledInit() {
                robot.outputs.shooterMaster.set(0);
            }
        });

        robot.operator.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (!robot.outputs.shooterMaster.isControlEnabled()) {

                    robot.outputs.shooterMaster.enableControl();
                    robot.outputs.shooterMaster.set(targetRPM);

                } else {

                    robot.outputs.shooterMaster.set(0);
                    robot.outputs.shooterMaster.disableControl();
                }
            }
        });


        robot.operator.B.addListener(new ButtonListener() { //TODO: Add targeting

            @Override public void pressed() {

                if (robot.outputs.shooterMaster.getError() < targetError) {

                    System.out.println("<Shooter> Beginning to shoot.");

                    robot.elevator.run();

                    robot.runNextLoop(() -> { if (!robot.driver.RB.get()) robot.elevator.stop(); }, 500);
                }
            }

            @Override public void held() {

                if (robot.outputs.shooterMaster.getError() < targetError)
                    System.out.println("<Shooter> Beginning to shoot.");

                Evaluable evaluable = new Evaluable() {

                    @Override public void eval() {

                        if (robot.outputs.shooterMaster.getError() > targetError) {

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

                robot.outputs.shooterMaster.set(0);
                robot.outputs.shooterMaster.disableControl();

                System.out.println("<Shooter> Stopping the shooter.");
            }
        });

        robot.operator.UP.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM += 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooterMaster.get() != 0) robot.outputs.shooterMaster.set(targetRPM);
            }
        });

        robot.operator.DOWN.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM -= 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooterMaster.get() != 0) robot.outputs.shooterMaster.set(targetRPM);
            }
        });

        robot.operator.LH.addListener((value -> robot.outputs.shooterPivot.set(Math.abs(value) > 0.1 ? value * -0.2 : 0)));
    }
}