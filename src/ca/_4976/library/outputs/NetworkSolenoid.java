package ca._4976.library.outputs;

import ca._4976.library.AsynchronousRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class NetworkSolenoid extends TimedSolenoid {

	ITable table;

	public NetworkSolenoid(AsynchronousRobot module, int pcmId, int outPin, int inPin) {
		super(module, pcmId, outPin, inPin);

		table = NetworkTable.getTable("Debug").getSubTable("Solenoids").getSubTable("Double: " + inPin + "," + outPin);
	}

	@Override public void output(boolean extended) {

		table.putBoolean("Extended", extended);
	}

	@Override public boolean isExtended() { return table.getBoolean("Extended", false); }
}
