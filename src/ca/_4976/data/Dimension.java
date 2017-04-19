package ca._4976.data;

public class Dimension {

	public double width, height;

	public Dimension(double width, double height) {

		this.width = width;
		this.height = height;
	}

	public double getWidth() { return width; }

	public double getHeight() { return height; }

	public int[] asIntArray() { return new int[] { (int) width, (int) height }; }
}
