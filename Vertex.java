import java.util.Optional;

class Vertex {
    private final int id;
    private final Point p;
    private Optional<HalfEdge> halfedge;

    Vertex(int id, Point p) {
        this(id, p, Optional.<HalfEdge>empty());
    }

    private Vertex(int id, Point p, Optional<HalfEdge> halfedge) {
        this.id = id;
        this.p = p;
        this.halfedge = halfedge;
    }

    void setHalfEdge(HalfEdge halfedge) {
        this.halfedge = Optional.<HalfEdge>of(halfedge);
    }

    Vertex copy() {
        return new Vertex(this.id, this.p, this.halfedge);
    }

    Vertex move(Point p) {
        return new Vertex(this.id, p, this.halfedge); 
    }

    int getId() {
        return this.id;
    }

    Point getPoint() {
        return this.p;
    }

    Optional<HalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    double getX() {
        return this.p.getX();
    }

    double getY() {
        return this.p.getY();
    }

    Vertex translateCoords(double x, double y) {
        return new Vertex(this.id,
                this.p.translateCoords(x, y), this.halfedge);
    }

    Vertex translateAngle(double angle, double dist) {
        return new Vertex(this.id,
                this.p.translateAngle(angle, dist), this.halfedge);
    }

    double angle() {
        return this.p.angle();
    }

    double angleBetween(Vertex other) {
        return this.p.angleBetween(other.p);
    }

    double distanceBetween(Vertex other) {
        return this.p.distanceBetween(other.p);
    }

    double distanceFromOrigin() {
        return this.p.distanceFromOrigin();
    }

    Point midpoint(Vertex other) {
        return this.p.midpoint(other.p);
    }

    Point lerp(Vertex other, double ratio) {
        return this.p.lerp(other.p, ratio);
    }

    @Override
    public String toString() {
        HalfEdge edge = new HalfEdge(-1);
        return String.format("Vertex %d: %s, Edge %d", this.id, this.p.toString(),
                this.halfedge.orElse(edge).getId());
    }
}
