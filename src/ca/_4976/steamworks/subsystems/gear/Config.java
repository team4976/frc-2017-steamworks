package ca._4976.steamworks.subsystems.gear;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Gear Handler");

	double intakeSpeed = getKey(table, "Intake Speed (%)", 0.5);
	double releaseSpeed = getKey(table, "Release Speed (%)", 0.5);
	double gripSpeed = getKey(table, "Grip Speed (%)", 0.1);

	int gripDelay = getKey(table, "Grip Delay (MILLIS)", 1000);
	int raiseDelay = getKey(table, "Raise Delay (MILLIS)", 1500);

	int releaseTime = getKey(table, "Release Time (MILLIS)", 300);

	double currentLimit = getKey(table, "Current Threshold (AMPS)", 15.0);

	Config() {

		table.addTableListener(((source, key, value, isNew) -> {

			intakeSpeed = getKey(table, "Intake Speed (%)", 0.5);
			releaseSpeed = getKey(table, "Release Speed (%)", 0.5);
			gripSpeed = getKey(table, "Grip Speed (%)", 0.1);
			gripDelay = getKey(table, "Grip Delay (MILLIS)", 1000);
			raiseDelay = getKey(table, "Raise Delay (MILLIS)", 1500);
			releaseTime = getKey(table, "Release Time (MILLIS)", 300);
			currentLimit = getKey(table, "Current Threshold (AMPS)", 15.0);

		}));
	}
}
