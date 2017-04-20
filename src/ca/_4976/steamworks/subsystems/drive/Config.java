package ca._4976.steamworks.subsystems.drive;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Drive");

	double[] linearRamp = getKey(table, "Linear Ramp (% PER SECOND)", new double[] { 4.0, 4.0 });
	double[] rotationalRamp = getKey(table, "Rotational Ramp (% PER SECOND)", new double[] { 4.0, 4.0 });

	Config() {

		for (int i = 0; i < linearRamp.length; i++) linearRamp[i] /= 200;
		for (int i = 0; i < rotationalRamp.length; i++) rotationalRamp[i] /= 200;

		table.addTableListener(((source, key, value, isNew) -> {

			System.out.println("<Drive> " + key + " was changed");

			linearRamp = getKey(table, "Linear Ramp (% PER SECOND)", new double[] { 4.0, 4.0 });
			rotationalRamp = getKey(table, "Rotational Ramp (% PER SECOND)", new double[] { 4.0, 4.0 });

			for (int i = 0; i < linearRamp.length; i++) linearRamp[i] /= 200;
			for (int i = 0; i < rotationalRamp.length; i++) rotationalRamp[i] /= 200;
		}));
	}
}
