package ca._4976.library.outputs;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class NetworkVictorSP extends VictorSP {

	private ITable table;

	public NetworkVictorSP(int channel) {

		super(channel);

		table = NetworkTable.getTable("Debug").getSubTable("Motors").getSubTable("Victor: " + channel);
	}

	@Override public void set(double speed) { table.putNumber("Speed", speed); }

	@Override public double get() { return table.getNumber("Speed", 0); }
}
