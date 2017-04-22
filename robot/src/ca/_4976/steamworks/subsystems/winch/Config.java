package ca._4976.steamworks.subsystems.winch;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Winch");

	double holdSpeed = getKey(table, "Hold Speed (%)", 0.34);

	Config() {

		table.addTableListener((source, key, value, isNew) -> {

			System.out.println("<Winch> " + key + " was changed: " + value);

			holdSpeed = getKey(table, "Hold Speed (%)", 0.34);
		});
	}
}
