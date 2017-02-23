package ca._4976.steamworks.subsystems.profiler;


import ca._4976.library.controllers.Button;

public class Config {

    final double kP = 20, kI = 0, kD = 0;

    final double tickTime = 1000000000 / 200;

    private final static Config config = new Config();

    static Config getInstance() { return config; }
}
