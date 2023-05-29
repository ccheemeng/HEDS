import java.util.Optional;

class Vertex {
    private final int id;
    private final Point p;
    private Optional<IHalfEdge> halfedge;

    Vertex(int id, Point p) {
        this(id, p, Optional.<IHalfEdge>empty());
    }

    private Vertex(int id, Point p, Optional<IHalfEdge> halfedge) {
        this.id = id;
        this.p = p;
        this.halfedge = halfedge;
    }

    void setHalfEdge(IHalfEdge halfedge) {
        this.halfedge = Optional.<IHalfEdge>of(halfedge);
    }

    Vertex setPoint(Point p) {
        return new Vertex(this.id, p, this.halfedge); 
    }

    int getId() {
        return this.id;
    }

    Point getPoint() {
        return this.p;
    }

    Optional<IHalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    @Override
    public String toString() {
        return String.format("Vertex %d: %s, Edge %d", this.id, this.p.toString(),
                this.halfedge.orElse(new HalfEdge(-1)).getId());
    }
}
