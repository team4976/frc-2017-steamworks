package ca._4976.library.math;

public class Vector2D {

    private double x, y;

    public Vector2D(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setX(double x) { this.x = x; }

    public void setY(double y) { this.y = y; }

    public void add(double x, double y) {

        this.x += x;
        this.y += y;
    }

    public void sub(double x, double y) {

        this.x -= x;
        this.y -= y;
    }

    public void add(Vector2D vector2D) { add(vector2D.getX(), vector2D.getY());}

    public void sub(Vector2D vector2D) { sub(vector2D.getX(), vector2D.getY());}


}
