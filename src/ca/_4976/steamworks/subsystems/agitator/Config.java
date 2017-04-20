package ca._4976.steamworks.subsystems.agitator;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Agitator");

	int targetCurrent = getKey(table, "Target Current (MILLI AMPS)", 10000);
	double reverseSpeed = getKey(table, "Target Reversed Speed (%)", 1.0);

	Config() {

		table.addTableListener((source, key, value, isNew) -> {

			System.out.println("<Agitator> " + key + " was changed: " + value);

			targetCurrent = getKey(table, "Target Current (MILLI AMPS)", 10000);
			reverseSpeed = getKey(table, "Target Reversed Speed (%)", 1.0);

			runListener();
		});
	}
}
