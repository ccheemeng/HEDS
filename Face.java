import java.util.Optional;

public class Face {
    private final int id;
    private Optional<HalfEdge> halfedge;

    Face(int id) {
        this.id = id;
        this.halfedge = Optional.<HalfEdge>empty();
    }

    private Face(int id, Optional<HalfEdge> halfedge) {
        this.id = id;
        this.halfedge = halfedge;
    }

    void setHalfEdge(HalfEdge halfedge) {
        this.halfedge = Optional.<HalfEdge>of(halfedge);
    }

    void removeHalfEdge() {
        this.halfedge = Optional.<HalfEdge>empty();
    }

    Face copy() {
        return new Face(this.id, this.halfedge);
    }

    int getId() {
        return this.id;
    }

    public Optional<HalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    @Override
    public String toString() {
        HalfEdge edge = new HalfEdge(-1);
        return String.format("Face %d: Edge %d", this.id,
                this.halfedge.orElse(edge).getId());
    }
}
