package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.Initialization;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.lang.annotation.Target;

public class Shooter {

    private NetworkTable table = NetworkTable.getTable("Shooter");

    private double targetRPM = table.getSubTable("Shot One").getNumber("Target (RPM)", 3100);
    private double targetError = table.getSubTable("Shot One").getNumber("Target Error (RPM)", 100);
    private double increment = table.getSubTable("Shot One").getNumber("Change Increment", 50);

    public Shooter(Robot robot) {

        table.getSubTable("Shot One").putNumber("Target (RPM)", targetRPM);
        table.getSubTable("Shot One").putNumber("Target Error (RPM)", targetError);
        table.getSubTable("Shot One").putNumber("Change Increment", increment);

        robot.addListener(new RobotStateListener() {

            @Override public void robotInit() {

                Initialization.HARDWARE_INPUT_EVALS.add(() -> {

                    NetworkTable.getTable("Status").putNumber("Shooter Speed (RPM)", robot.outputs.shooter.getSpeed());
                    NetworkTable.getTable("Status").putNumber("Shooter Error (RPM)", robot.outputs.shooter.getError());
                });
            }

            @Override public void disabledInit() {
                robot.outputs.shooter.set(0);
            }
        });

        robot.operator.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (robot.outputs.shooter.get() == 0) {

                    System.out.println("<Shooter> Priming the Shooter.");

                    robot.vision.unpause();
                    robot.outputs.shooter.changeControlMode(CANTalon.TalonControlMode.Speed);
                    robot.outputs.shooter.set(targetRPM);

                } else {

                    System.out.println("<Shooter> Stopping the Shooter.");

                    robot.vision.pause();
                    robot.outputs.shooter.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                    robot.outputs.shooter.set(0);
                }
            }
        });

        robot.operator.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (robot.outputs.shooter.getError() < targetError) {

                    System.out.println("<Shooter> Beginning to shoot.");

                    robot.elevator.run();

                    robot.runNextLoop(() -> { if (!robot.driver.RB.get()) robot.elevator.stop(); }, 100);
                }
            }

            @Override public void held() {

                if (robot.outputs.shooter.getError() < targetError) System.out.println("<Shooter> Beginning to shoot.");

                else System.err.println("<Shooter> WARN: (RPM) too low to fire.");

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

            @Override public void falling() {

                System.out.println("<Shooter> Finished shooting.");

                robot.elevator.stop();
            }
        });

        robot.operator.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.outputs.shooter.set(0);


                System.out.println("<Shooter> Stopping the shooter.");
            }
        });

        robot.operator.LEFT.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM = table.getSubTable("Shot One").getNumber("Target (RPM)", 3100);
                targetError = table.getSubTable("Shot One").getNumber("Target Error (RPM)", 100);
                increment = table.getSubTable("Shot One").getNumber("Change Increment", 50);

                System.out.println("<Shooter> Setting the Target Speed (ROM) to " + targetRPM);

                table.getSubTable("Shot One").putNumber("Target (RPM)", targetRPM);
                table.getSubTable("Shot One").putNumber("Target Error (RPM)", targetError);
                table.getSubTable("Shot One").putNumber("Change Increment", increment);
            }
        });

        robot.operator.UP.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM = table.getSubTable("Shot Two").getNumber("Target (RPM)", 3100);
                targetError = table.getSubTable("Shot Two").getNumber("Target Error (RPM)", 100);
                increment = table.getSubTable("Shot Two").getNumber("Change Increment", 50);

                System.out.println("<Shooter> Setting the Target Speed (ROM) to " + targetRPM);

                table.getSubTable("Shot Two").putNumber("Target (RPM)", targetRPM);
                table.getSubTable("Shot Two").putNumber("Target Error (RPM)", targetError);
                table.getSubTable("Shot Two").putNumber("Change Increment", increment);
            }
        });

        robot.operator.RIGHT.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM = table.getSubTable("Shot Three").getNumber("Target (RPM)", 3100);
                targetError = table.getSubTable("Shot Three").getNumber("Target Error (RPM)", 100);
                increment = table.getSubTable("Shot Three").getNumber("Change Increment", 50);System.out.println("<Shooter> Setting the Target Speed (ROM) to " + targetRPM);

                System.out.println("<Shooter> Setting the Target Speed (ROM) to " + targetRPM);

                table.getSubTable("Shot Three").putNumber("Target (RPM)", targetRPM);
                table.getSubTable("Shot Three").putNumber("Target Error (RPM)", targetError);
                table.getSubTable("Shot Three").putNumber("Change Increment", increment);
            }
        });

        robot.operator.LH.addListener(value -> robot.outputs.pivot.set(Math.abs(value) > 0.3 ? value * -0.2 : 0));

        robot.operator.LV.addListener(value -> new Evaluable() {

            @Override public void eval() {

                if (Math.abs(robot.operator.LV.get()) > 0.1 && value == robot.operator.LV.get()) {

                    targetRPM += increment * value < 0 ? 1 : -1;
                    if (robot.outputs.shooter.get() != 0) robot.outputs.shooter.set(targetRPM);
                    System.out.println("<Shooter> The Target Speed (RPM) was set to " + (int) targetRPM);

                    robot.runNextLoop(this, (int) (400 / robot.operator.LV.get()));
                }
            }

        }.eval());
    }
}