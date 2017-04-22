package ca._4976.steamworks.subsystems.vision.goal;

import ca._4976.data.Dimension;
import ca._4976.library.ConfigBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Config extends ConfigBase {


	private ITable table = NetworkTable.getTable("Vision").getSubTable("Goal");
	private ITable filter = table.getSubTable("Filter Contours");
	private ITable pid = table.getSubTable("PID");
	private ITable threshold = table.getSubTable("HSV Threshold");

	double kP = getKey(pid, "P", 0.007);
	double kI = getKey(pid, "I", 0.0);
	double kD = getKey(pid, "D", 0.004);
	double offset = getKey(table, "Offset (PIXELS)", 20);
	Dimension resolution = getKey(table, "Resolution (PIXELS)", new Dimension(160, 120));
	int exposure = getKey(table, "Exposure (UNIT)", 0);
	int brightness = getKey(table, "Brightness (UNIT)", 0);
	int whiteBalance = getKey(table, "White Balance (UNIT)", 0);

	double[] hsvThresholdHue = {
			getKey(threshold, "Min Hue", 60),
			getKey(threshold, "Max Hue", 70)
	};

	double[] hsvThresholdSaturation = {
			getKey(threshold, "Min Saturation", 115),
			getKey(threshold, "Max Saturation", 255)
	};

	double[] hsvThresholdValue = {
			getKey(threshold, "Min Value", 40),
			getKey(threshold, "Max Value", 255)
	};

	double filterContoursMinArea = getKey(filter, "Min Area (PIXELS)", 0.0);
	double filterContoursMinPerimeter = getKey(filter, "Min Perimeter (PIXELS)", 30.0);
	double filterContoursMinWidth = getKey(filter, "Min Width (PIXELS)", 0.0);
	double filterContoursMaxWidth = getKey(filter, "Max Width (PIXELS)", 1000.0);
	double filterContoursMinHeight = getKey(filter, "Min Height (PIXELS)", 0.0);
	double filterContoursMaxHeight = getKey(filter, "Max Height (PIXELS)", 1000.0);

	double[] filterContoursSolidity = {
			getKey(filter, "Min Solidity (%)", 0.0),
			getKey(filter, "Max Solidity (%)", 100.0)
	};

	double filterContoursMaxVertices = getKey(filter, "Max Vertices", 1000000.0);
	double filterContoursMinVertices = getKey(filter, "Min Vertices", 0.0);
	double filterContoursMinRatio = getKey(filter, "Min Ratio", 0.0);
	double filterContoursMaxRatio = getKey(filter, "Max Vertices", 0.0);


	Config() {

		pid.addSubTableListener((source, key, value, isNew) -> {

			System.out.println("<Goal Tracker> PID " + key + " was changed: " + value);

			kP = getKey(pid, "P", 0.007);
			kI = getKey(pid, "I", 0.0);
			kD = getKey(pid, "D", 0.004);
			runListener();
		});

		filter.addTableListener(((source, key, value, isNew) -> {

			System.out.println("<Goal Tracker> Filter Contours " + key + " was changed: " + value);

			filterContoursMinArea = getKey(filter, "Min Area (PIXELS)", 0.0);
			filterContoursMinPerimeter = getKey(filter, "Min Perimeter (PIXELS)", 30.0);
			filterContoursMinWidth = getKey(filter, "Min Width (PIXELS)", 0.0);
			filterContoursMaxWidth = getKey(filter, "Max Width (PIXELS)", 1000.0);
			filterContoursMinHeight = getKey(filter, "Min Height (PIXELS)", 0.0);
			filterContoursMaxHeight = getKey(filter, "Max Height (PIXELS)", 1000.0);
			filterContoursSolidity[0] = getKey(filter, "Min Solidity (%)", 0.0);
			filterContoursSolidity[1] = getKey(filter, "Max Solidity (%)", 100.0);
			filterContoursMaxVertices = getKey(filter, "Max Vertices", 1000000.0);
			filterContoursMinVertices = getKey(filter, "Min Vertices", 0.0);
			filterContoursMinRatio = getKey(filter, "Min Ratio", 0.0);
			filterContoursMaxRatio = getKey(filter, "Max Vertices", 0.0);

		}));

		table.addTableListener((source, key, value, isNew) -> {

			System.out.println("<Goal Tracker> " + key + " was changed: " + value);

			offset = getKey(table, "Offset (PIXELS)", 1);
			resolution = getKey(table, "Resolution (PIXELS)", new Dimension(160, 120));
			exposure = getKey(table, "Exposure (UNIT)", 0);
			brightness = getKey(table, "Brightness (UNIT)", 0);
			whiteBalance = getKey(table, "White Balance (UNIT)", 0);

			if (key.equals("Resolution (PIXELS)")) {

				if (resolution.getWidth() / resolution.getHeight() == 4 / 3)
					runListener();

			} else runListener();
		});
	}
}
