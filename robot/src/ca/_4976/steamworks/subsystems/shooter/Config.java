package ca._4976.steamworks.subsystems.shooter;

import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Config extends ConfigBase {

	private NetworkTable table = NetworkTable.getTable("Shooter");

	double[] targetSpeed = new double[5];
	double[] targetError = new double[5];
	double[] hoodPosition = new double[5];
	double[] turretPosition = new double[5];

	Config() {

		for (int x = 0; x < 4; x++) {

			ITable shot = table.getSubTable("Shot " + x);

			targetSpeed[x] = getKey(shot, "Target Speed (RPM)", 3100);
			targetError[x] = getKey(shot, "Target Error (RPM)", 100);
			hoodPosition[x] = getKey(shot, "Hood Position (%)", 0.2);
			turretPosition[x] = getKey(shot, "Turret Position (UNITS)", 0.0);

			int iteration = x;

			shot.addTableListener((source, key, value, isNew) -> {

				System.out.println("<Shooter> Shot " + iteration + " " + key + " was changed: " + value);

				for (int y = 0; y < 4; y++) {

					ITable temp = table.getSubTable("Shot " + y);

					targetSpeed[y] = getKey(temp, "Target Speed (RPM)", 3100);
					targetError[y] = getKey(temp, "Target Error (RPM)", 100);
					hoodPosition[y] = getKey(temp, "Hood Position (%)", 0.2);
					turretPosition[y] = getKey(temp, "Turret Position (UNITS)", 0.0);
				}

				runListener();
			});
		}
	}
}
