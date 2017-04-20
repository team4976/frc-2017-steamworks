package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Motion Control");
	private ITable pid = table.getSubTable("PID");
	private static Config config = null;

	public final double tickTime = 1000000000 / 200;

	double kP = getKey(pid, "P", -1.0);
	double kI = getKey(pid, "I", 0.0);
	double kD = getKey(pid, "D", 0.0);

	boolean runShooterAtStart = getKey(table, "Run Shooter at Start", true);
	boolean extendWinchArmAtStart = getKey(table, "Extend Winch at Start", true);

	String loadTable = getKey(table, "load_table", "");

	public static Config getInstance() {

		if (config == null) config = new Config();

		return config;
	}

	private Config() {

		pid.addTableListener((source, key, value, isNew) -> {

			System.out.println("<Motion Control> PID " + key + " was changed: " + value);

			kP = getKey(pid, "P", -1.0);
			kI = getKey(pid, "I", 0.0);
			kD = getKey(pid, "D", 0.0);
		});

		table.addTableListener((source, key, value, isNew) -> {

			if (key.equals("load_table")) {

				System.out.println("<Motion Control> Autonomous mode selected.");
				loadTable = getKey(table, "load_table", "");
				runListener();
			}
		});
	}
}
