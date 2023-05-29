/**
 * A wrapper class of two doubles representing a 2D Point.
 *
 * @param x the double representing the x position.
 * @param y the double representing the y position.
 */
public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Point(%f, %f)", this.x, this.y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
