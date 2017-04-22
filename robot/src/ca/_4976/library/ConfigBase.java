package ca._4976.library;

import ca._4976.data.Dimension;
import edu.wpi.first.wpilibj.tables.ITable;

public class ConfigBase {

	private Evaluable listener = () -> { };

	protected Dimension getKey(ITable table, String key, Dimension back) {

		if (table.containsKey(key))
			return new Dimension(table.getNumberArray(key, back.asDoubleArray()));

		table.putNumberArray(key, back.asDoubleArray());

		return back;
	}

	protected double[] getKey(ITable table, String key, double[] back) {

		if (table.containsKey(key)) return table.getNumberArray(key, back);

		table.putNumberArray(key, back);

		return back;
	}


	protected double getKey(ITable table, String key, double back) {

		if (table.containsKey(key)) return table.getNumber(key, back);

		table.putNumber(key, back);

		return back;
	}

	protected boolean getKey(ITable table, String key, boolean back) {

		if (table.containsKey(key)) return table.getBoolean(key, back);

		table.putBoolean(key, back);

		return back;
	}

	protected int getKey(ITable table, String key, int back) {

		if (table.containsKey(key)) return (int) table.getNumber(key, back);

		table.putNumber(key, back);

		return back;
	}

	protected String getKey(ITable table, String key, String back) {

		if (table.containsKey(key)) return table.getString(key, back);

		table.putString(key, back);

		return back;
	}

	public void setListener(Evaluable evaluable) { listener = evaluable; }

	public void runListener() { listener.eval(); }
}
