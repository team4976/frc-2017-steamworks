package ca._4976.steamworks.subsystems.profiler;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

class Config {

    private NetworkTable table = NetworkTable.getTable("Motion Control");
    double kP = -10, kI = 0, kD = 0;

    final double tickTime = 1000000000 / 200;

    private final static Config config = new Config();

    static Config getInstance() { return config; }

    private Config() {

        if (table.containsKey("kP")) {

            kP = table.getNumber("kP", 0);

        } else {

            table.putNumber("kP", 0);
            kP = 0;
        }

        if (table.containsKey("kI")) {

            kI = table.getNumber("kI", 0);

        } else {

            table.putNumber("kI", 0);
            kI = 0;
        }

        if (table.containsKey("kD")) {

            kD = table.getNumber("kD", 0);

        } else {

            table.putNumber("kI", 0);
            kD = 0;
        }

        table.addTableListener((source, key, value, isNew) -> {

            if (key.equals("load_table")) System.out.println("<Motion Control> Autonomous mode selected.");

           switch (key) {

                case "kP": kP = (double) value; break;
                case "kI": kI = (double) value; break;
                case "kD": kD = (double) value; break;
            }
        });
    }
}
