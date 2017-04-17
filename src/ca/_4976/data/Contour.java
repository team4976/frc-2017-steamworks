package ca._4976.data;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Contour {

	public final Position position;
	public final double area;
	public final double height;
	public final double width;

	public class Position {

		public final Point center;
		public final Point bottomLeftCorner;
		public final double leftEdge;
		public final double rightEdge;
		public final double bottomEdge;
		public final double topEdge;

		private Position(Rect rect) {

			bottomLeftCorner = new Point(rect.x, rect.y);

			leftEdge = bottomLeftCorner.x;
			rightEdge = leftEdge + width;
			bottomEdge = bottomLeftCorner.y;
			topEdge = bottomEdge + height;

			center = new Point((int) (leftEdge + (width / 2)), (int) (rightEdge + (height / 2)));
		}
	}

	public Contour(MatOfPoint contour) {

		area = Imgproc.contourArea(contour);
		Rect rect = Imgproc.boundingRect(contour);

		width = rect.width;
		height = rect.height;

		position = new Position(rect);
	}

	public static Contour getLargest(ArrayList<MatOfPoint> contours) {

		contours.sort(Comparator.comparingDouble(Imgproc::contourArea));

		return new Contour(contours.get(contours.size() - 1));
	}
}
