package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Shooter {

    private Config config = new Config();
    private Robot robot;

    private int selection = 0;

    public Shooter(Robot robot) {

        this.robot = robot;

        robot.addListener(new RobotStateListener() {

            @Override public void robotInit() { }

            @Override public void disabledInit() {

                robot.vision.pause();
                robot.outputs.shooter.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                robot.outputs.shooter.set(0);
            }
        });

        robot.operator.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (robot.outputs.shooter.get() == 0) {

                    System.out.println("<Shooter> Priming the Shooter.");

                    robot.vision.unpause();
                    robot.outputs.shooter.changeControlMode(CANTalon.TalonControlMode.Speed);
                    robot.outputs.shooterSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
                    robot.outputs.shooterSlave.set(12);
                    robot.outputs.shooter.set(config.targetSpeed[selection]);

                } else if (robot.vision.isPaused()) {

                    System.out.println("<Shooter> Looking for target.");
                    robot.vision.unpause();

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

                if (robot.outputs.shooter.getError() < config.targetError[selection]) {

                    System.out.println("<Shooter> Beginning to shoot.");

                    robot.elevator.run();

                    robot.runNextLoop(() -> { if (!robot.driver.RB.get()) robot.elevator.stop(); }, 100);
                }
            }

            @Override public void held() {

                if (robot.outputs.shooter.getError() < config.targetError[selection]) System.out.println("<Shooter> Beginning to shoot.");

                else System.err.println("<Shooter> WARN: (RPM) too low to fire.");

                new Evaluable() {

                    @Override public void eval() {

                        if (robot.outputs.shooter.getError() > config.targetError[selection]) {

                            if (robot.elevator.isRunning())
                                System.err.println("<Shooter> WARN: (RPM) too low to fire.");

                            robot.elevator.stop();

                        } else robot.elevator.run();

                        if (robot.driver.RB.get()) robot.runNextLoop(this);
                    }

                }.eval();
            }

            @Override public void falling() {

                System.out.println("<Shooter> Finished shooting.");

                robot.elevator.stop();
            }
        });

        robot.operator.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.vision.pause();
                robot.outputs.shooter.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                robot.outputs.shooter.set(0);

                System.out.println("<Shooter> Stopping the shooter.");
            }
        });

        robot.operator.LEFT.addListener(new ButtonListener() {

            @Override public void pressed() {

                selection = 0;
                config.update();
            }
        });

        robot.operator.UP.addListener(new ButtonListener() {

            @Override public void pressed() {

                selection  = 1;
                config.update();
            }
        });

        robot.operator.RIGHT.addListener(new ButtonListener() {

            @Override public void pressed() {

                selection = 2;
                config.update();
            }
        });

        robot.operator.DOWN.addListener(new ButtonListener() {

            @Override public void pressed() {

                selection = 3;
                config.update();
            }
        });

        robot.operator.LH.addListener(value -> robot.outputs.pivot.set(Math.abs(value) > 0.4 ? value * -0.3 : 0));

        robot.operator.LV.addListener(value -> new Evaluable() {

            @Override public void eval() {

                if (value == robot.operator.LV.get() && value != 0) {

                    config.targetSpeed[selection] = config.targetSpeed[selection] - value;

                    robot.runNextLoop(this);

                } else if (value == 0) {

                    config.targetSpeed[selection] = ((int) config.targetSpeed[selection] / 10) * 10;
                }
            }

        }.eval());
    }

    private class Config {

        private NetworkTable table = NetworkTable.getTable("Shooter");

        private double kP, kI, kD, kF, kRamp;
        private int kIZone, kProfile;

        private double[] targetSpeed = new double[4];
        private double[] targetError = new double[4];
        private double[] hoodPosition = new double[4];
        private double[] turretPosition = new double[4];

        private Config() {

            ITable pid = table.getSubTable("PID");

            if (pid.containsKey("kP")) kP = pid.getNumber("kP", 0);

            else {

                kP = 0;
                pid.putNumber("kP", 0);
            }

            if (pid.containsKey("kI")) kI = pid.getNumber("kI", 0);

            else {

                kI = 0;
                pid.putNumber("kI", 0);
            }

            if (pid.containsKey("kD")) kD = pid.getNumber("kD", 0);

            else {

                kD = 0;
                pid.putNumber("kD", 0);
            }

            if (pid.containsKey("kF")) kF = pid.getNumber("kF", 0);

            else {

                kF = 0;
                pid.putNumber("kF", 0);
            }

            if (pid.containsKey("kIZone")) kIZone = (int) pid.getNumber("kIZone", 0);

            else {

                kIZone = 0;
                pid.putNumber("kIZone", 0);
            }

            if (pid.containsKey("kRamp")) kRamp = pid.getNumber("kRamp", 0);

            else {

                kRamp = 0;
                pid.putNumber("kRamp", 0);
            }

            if (pid.containsKey("kProfile")) kProfile = (int) pid.getNumber("kProfile", 0);

            else {

                kProfile = 0;
                pid.putNumber("kProfile", 0);
            }

            pid.addTableListener((source, key, value, isNew) -> {

                switch (key) {

                    case "kP":kP = (double) value; break;
                    case "kI": kI = (double) value; break;
                    case "kD": kD = (double) value; break;
                    case "kF": kF = (double) value; break;
                    case "kIZone": kIZone = (int) (double) value; break;
                    case "kRamp": kRamp = (double) value; break;
                    case "kProfile": kProfile = (int) (double) value; break;
                }

                robot.outputs.shooter.setPID(kP, kI, kD, kF, kIZone, kRamp, kProfile);
            });

            for (int i = 0;  i < 4; i++) {

                ITable subTable = table.getSubTable("Shot " + i);

                if (subTable.containsKey("Target Speed (RPM)"))
                    targetSpeed[i] = subTable.getNumber("Target Speed (RPM)", 3100);

                else {

                    subTable.putNumber("Target Speed (RPM)", 3100);
                    targetSpeed[i] = 3100;
                }

                if (subTable.containsKey("Target Error (RPM)"))
                    targetError[i] = subTable.getNumber("Target Error (RPM)", 25);

                else {

                    subTable.putNumber("Target Error (RPM)", 25);
                    targetError[i] = 25;
                }

                if (subTable.containsKey("Hood Position (%)"))
                    hoodPosition[i] = subTable.getNumber("Hood Position (%)", 0.2);

                else {

                    subTable.putNumber("Hood Position (%)", 0.2);
                    hoodPosition[i] = 0.2;
                }

                if (subTable.containsKey("Turret Position (ANGLE)"))
                    turretPosition[i] = subTable.getNumber("Turret Position (ANGLE)", 0);

                else {

                    subTable.putNumber("Turret Position (ANGLE)", 0);
                    turretPosition[i] = 0;
                }

                int finalized = i;

                subTable.addTableListener((source, key, value, isNew) -> {

                    switch (key) {
                        case "Target Speed (RPM)": targetSpeed[finalized] = (double) value;
                        case "Target Error (RPM)": targetError[finalized] = (double) value;
                        case "Hood Position (%)": hoodPosition[finalized] = (double) value;
                        case "Turret Position (ANGLE)": turretPosition[finalized] = (double) value;
                    }

                    if (finalized == selection) update();
                });
            }
        }

        private void update() {

            System.out.println("<Shooter> Setting Target Speed (RPM): " + targetSpeed[selection]);
            System.out.println("<Shooter> Setting Hood Position (%): " + hoodPosition[selection]);
            System.out.println("<Shooter> Setting Turret Position (ANGLE): " + turretPosition[selection]);

            if (robot.outputs.shooter.get() != 0) robot.outputs.shooter.set(targetSpeed[selection]);

            robot.outputs.hood.set(hoodPosition[selection]);

            if (robot.vision.isPaused()) {

                robot.outputs.pivot.changeControlMode(CANTalon.TalonControlMode.Position);
                robot.outputs.pivot.set(turretPosition[selection]);
            }
        }
    }
}