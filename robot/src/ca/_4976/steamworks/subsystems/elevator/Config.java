package ca._4976.steamworks.subsystems.elevator;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Elevator");

	double speed = getKey(table, "Speed (%)", 1.0);

	Config() {

		table.addTableListener((source, key, value, isNew) -> {

			System.out.println("<Elevator> " + key + " was changed: " + value);

			speed = getKey(table, "Speed (%)", 1.0);

			runListener();
		});
	}
}
