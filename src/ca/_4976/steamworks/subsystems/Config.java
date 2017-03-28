package ca._4976.steamworks.subsystems;

import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Config {

    private Robot robot;

    Shooter shooter = new Shooter();
    Agitator agitator = new Agitator();
    Elevator elevator = new Elevator();
    Drive drive = new Drive();
    Winch winch = new Winch();
    Vision vision = new Vision();

    public Motion motion = new Motion();

    public Config(Robot robot) { this.robot = robot; }

    private double getKey(ITable table, String key, double back) {

        if (table.containsKey(key)) return table.getNumber(key, back);

        table.putNumber(key, back);

        return back;
    }

    private int getKey(ITable table, String key, int back) {

        if (table.containsKey(key)) return (int) table.getNumber(key, back);

        table.putNumber(key, back);

        return back;
    }

    public class Motion {

        private NetworkTable table = NetworkTable.getTable("Motion Control");
        private ITable pid = table.getSubTable("PID");

        public final double tickTime = 1000000000 / 200;

        public double kP = getKey(pid, "P", -1.0);
        public double kI = getKey(pid, "I", 0.0);
        public double kD = getKey(pid, "D", 0.0);

        private Motion() {

            pid.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Motion Control> PID " + key + " was changed: " + value);

                kP = getKey(pid, "P", -1.0);
                kI = getKey(pid, "I", 0.0);
                kD = getKey(pid, "D", 0.0);
            });

            table.addTableListener((source, key, value, isNew) -> {

                if (key.equals("load_table")) {

                    System.out.println("<Motion Control> Autonomous mode selected.");
                    robot.profiler.loadTable();
                }
            });
        }
    }

    class Vision {

        private NetworkTable table = NetworkTable.getTable("Vision");

        double offset = getKey(table, "Offset (PIXELS)", 20);

        private Vision() {

            table.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Elevator> " + key + " was changed: " + value);

                offset = getKey(table, "Offset (PIXELS)", 1);

                robot.vision.configNotify();
            });
        }
    }

    class Elevator {

        private NetworkTable table = NetworkTable.getTable("Elevator");

        double speed = getKey(table, "Speed (%)", 1);

        private Elevator() {

            table.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Elevator> " + key + " was changed: " + value);

                speed = getKey(table, "Speed (%)", 1);

                robot.elevator.configNotify();
            });
        }
    }

    class Winch {

        private NetworkTable table = NetworkTable.getTable("Winch");

        double holdSpeed = getKey(table, "Hold Speed (%)", 0.34);

        private Winch() {

            table.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Winch> " + key + " was changed: " + value);

                holdSpeed = getKey(table, "Hold Speed (%)", 0.34);
            });
        }
    }

    class Drive {

        private NetworkTable table = NetworkTable.getTable("Drive");

        double[] linearRamp = getKey(table, "Linear Ramp (% PER SECOND)", new double[] { 1.0, 1.0 });
        double[] rotationalRamp = getKey(table, "Rotational Ramp (% PER SECOND)", new double[] { 1.0, 1.0 });

        private Drive() {

            table.addTableListener(((source, key, value, isNew) -> {

                System.out.println("<Drive> " + key + " was changed");

                linearRamp = getKey(table, "Linear Ramp (% PER SECOND)", new double[] { 1.0, 1.0 });
                rotationalRamp = getKey(table, "Rotational Ramp (% PER SECOND)", new double[] { 1.0, 1.0 });
            }));
        }

        private double[] getKey(ITable iTable, String key, double[] back) {

            if (iTable.containsKey(key)) {

                double[] array = iTable.getNumberArray(key, back);

                return new double[] { array[0] / 200.0, array[1] / 200.0 };
            }

            iTable.putNumberArray(key, back);

            return new double[] { back[0] / 200.0, back[1] / 200.0 };
        }
    }

    class Agitator {

        private NetworkTable table = NetworkTable.getTable("Agitator");

        int targetCurrent = getKey(table, "Target Current (MILLI AMPS)", 10000);

        double reverseSpeed = getKey(table, "Target Reversed Speed (%)", 1.0);

        private Agitator() {

            table.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Agitator> " + key + " was changed: " + value);

                targetCurrent = getKey(table, "Target Current (MILLI AMPS)", 10000);
                reverseSpeed = getKey(table, "Target Reversed Speed (%)", 1.0);

                robot.agitator.configNotify();

            });
        }
    }

    class Shooter {

        private NetworkTable table = NetworkTable.getTable("Shooter");
        private ITable pid = table.getSubTable("PID");

        double kP = getKey(pid, "P", 1.0);
        double kI = getKey(pid, "I", 0.0);
        double kD = getKey(pid, "D", 50.0);
        double kF = getKey(pid, "F", 0.57);
        double kRamp = getKey(pid, "Ramp", 0.0);

        int kIZone = getKey(pid, "IZone", 0);
        int kProfile = getKey(pid, "Profile", 0);

        double[] targetSpeed = new double[4];
        double[] targetError = new double[4];
        double[] hoodPosition = new double[4];
        double[] turretPosition = new double[4];

        private Shooter() {

            pid.addTableListener((source, key, value, isNew) -> {

                System.out.println("<Agitator> PID: " + key + " was changed: " + value);

                kP = getKey(pid, "P", 1.0);
                kI = getKey(pid, "I", 0.0);
                kD = getKey(pid, "D", 50.0);
                kF = getKey(pid, "F", 0.57);
                kRamp = getKey(pid, "Ramp", 0.0);
                kIZone = getKey(pid, "IZone", 0);
                kProfile = getKey(pid, "Profile", 0);

                robot.outputs.shooter.setPID(
                        kP,
                        kI,
                        kD,
                        kF,
                        kIZone,
                        kRamp,
                        kProfile
                );

            });

            for (int x = 0; x < 4; x++) {

                ITable shot = table.getSubTable("Shot " + x);

                targetSpeed[x] = getKey(shot, "Target Speed (RPM)", 3100);
                targetError[x] = getKey(shot, "Target Error (RPM)", 100);
                hoodPosition[x] = getKey(shot, "Hood Position (%)", 0.2);
                turretPosition[x] = getKey(shot, "Turret Position (UNITS)", 0.0);

                int finalized = x;

                shot.addTableListener((source, key, value, isNew) -> {

                    System.out.println("<Shooter> Shot " + finalized + " " + key + " was changed: " + value);

                    for (int y = 0; y < 4; y++) {

                        ITable temp = table.getSubTable("Shot " + y);

                        targetSpeed[y] = getKey(temp, "Target Speed (RPM)", 3100);
                        targetError[y] = getKey(temp, "Target Error (RPM)", 100);
                        hoodPosition[y] = getKey(temp, "Hood Position (%)", 0.2);
                        turretPosition[y] = getKey(temp, "Turret Position (UNITS)", 0.0);
                    }

                    robot.shooter.configNotify();

                });
            }
        }
    }
}
