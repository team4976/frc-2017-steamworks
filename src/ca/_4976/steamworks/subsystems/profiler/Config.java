package ca._4976.steamworks.subsystems.profiler;


import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config {

    double kP = -0, kI = 0, kD = 0;

    final double tickTime = 1000000000 / 200;

    private final static Config config = new Config();

    static Config getInstance() {

        config.kP = NetworkTable.getTable("Motion Control").getSubTable("PID").getNumber("kP", 0);
        NetworkTable.getTable("Motion Control").getSubTable("PID").putNumber("kP", config.kP);

        config.kI = NetworkTable.getTable("Motion Control").getSubTable("PID").getNumber("kI", 0);
        NetworkTable.getTable("Motion Control").getSubTable("PID").putNumber("kP", config.kI);

        config.kD = NetworkTable.getTable("Motion Control").getSubTable("PID").getNumber("kD", 0);
        NetworkTable.getTable("Motion Control").getSubTable("PID").putNumber("kP", config.kD);

        return config;
    }
}
