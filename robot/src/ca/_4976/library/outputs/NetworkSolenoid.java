package ca._4976.library.outputs;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Created by Qormix on 4/19/2017.
 */
public class NetworkSolenoid extends Solenoid {

	ITable table;

	public NetworkSolenoid(int moduleNumber, int channel) {
		super(moduleNumber, channel);

		table = NetworkTable.getTable("Debug").getSubTable("Solenoids").getSubTable("Single: " + channel);

	}

	public NetworkSolenoid(int channel) {

		super(channel);

		table = NetworkTable.getTable("Debug").getSubTable("Solenoids").getSubTable("Single: " + channel);
	}

	@Override public void set(boolean output) { table.putBoolean("output", output); }
	@Override public boolean get() { return table.getBoolean("output", false); }
}
