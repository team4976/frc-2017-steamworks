package ca._4976.library.outputs;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class NetworkTalonSRX extends CANTalon {

	private ITable table;

	public NetworkTalonSRX(int deviceNumber) {

		super(deviceNumber);

		table = NetworkTable.getTable("Debug").getSubTable("Motors").getSubTable("Talon: " + deviceNumber);
	}

	@Override public void set(double speed) { table.putNumber("Speed", speed); }

	@Override public double get() { return table.getNumber("Speed", 0.0); }

	@Override public double getError() { return table.getNumber("Error", 0.0); }

	@Override public double getOutputVoltage() { return table.getNumber("Voltage", 0.0); }

	@Override public double getPosition() { return table.getNumber("Position", 0.0); }

	@Override public double getSpeed() { return table.getNumber("Speed", 0.0); }

	@Override public double getOutputCurrent() { return table.getNumber("Current", 0.0); }

	@Override public FeedbackDeviceStatus isSensorPresent(FeedbackDevice feedbackDevice) {

		return FeedbackDeviceStatus.FeedbackStatusNotPresent;
	}
}
