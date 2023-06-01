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

    public Point getPoint() {
        return this.p;
    }

    public Optional<HalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    @Override
    public String toString() {
        HalfEdge edge = new HalfEdge(-1);
        return String.format("Vertex %d: %s, Edge %d", this.id, this.p.toString(),
                this.halfedge.orElse(edge).getId());
    }
}
