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

    public Point translateCoords(double x, double y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point translateAngle(double angle, double dist) {
        return this.translateCoords(dist * Math.cos(angle),
                dist * Math.sin(angle));
    }

    /**
     * returns the angle of a vector drawn from
     * the origin to the point in radians.
     */
    public double angle() {
        double angle = Math.atan2(this.y, this.x);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public double angleBetween(Point other) {
        return other.angle() - this.angle();
    }

    public double distanceBetween(Point other) {
        return Math.sqrt(Math.pow(other.y - this.y, 2) +
                Math.pow(other.x - this.x, 2));
    }

    public double distanceFromOrigin() {
        return distanceBetween(new Point(0.0 ,0.0));
    }

    public Point midpoint(Point other) {
        return lerp(other, 0.5);
    }

    public Point lerp(Point other, double ratio) {
        double diffX = other.x - this.x;
        double diffY = other.y - this.y;
        return new Point(ratio * diffX, ratio * diffY);
    }
}
