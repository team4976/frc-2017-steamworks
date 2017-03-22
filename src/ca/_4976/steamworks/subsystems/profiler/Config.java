package ca._4976.steamworks.subsystems.profiler;


import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

class Config {

    private NetworkTable table = NetworkTable.getTable("Motion Control");
    private ITable subTable = table.getSubTable("PID");

    double kP = -10, kI = 0, kD = 0;

    final double tickTime = 1000000000 / 200;

    private final static Config config = new Config();

    static Config getInstance() { return config; }

    private Config() {

        if (subTable.containsKey("kP")) {

            kP = table.getNumber("kP", 0);

        } else {

            table.putNumber("kP", 0);
            kP = 0;
        }

        if (subTable.containsKey("kI")) {

            kI = table.getNumber("kI", 0);

        } else {

            table.putNumber("kI", 0);
            kI = 0;
        }

        if (subTable.containsKey("kD")) {

            kD = table.getNumber("kD", 0);

        } else {

            table.putNumber("kI", 0);
            kD = 0;
        }

        subTable.addTableListener((source, key, value, isNew) -> {

            switch (key) {

                case "kP": kP = (double) value; break;
                case "kI": kI = (double) value; break;
                case "kD": kD = (double) value; break;
            }
        });
    }
}
