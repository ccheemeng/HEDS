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

    Vertex move(Point p) {
        return new Vertex(this.id, p, this.halfedge); 
    }

    int getId() {
        return this.id;
    }

    public Point getPoint() {
        return this.p;
    }

    public Optional<IHalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    @Override
    public String toString() {
        IHalfEdge edge = () -> -1;
        return String.format("Vertex %d: %s, Edge %d", this.id, this.p.toString(),
                this.halfedge.orElse(edge).getId());
    }
}
