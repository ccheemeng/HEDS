import java.util.Optional;

class Face {
    private final int id;
    private Optional<IHalfEdge> halfedge;

    Face(int id) {
        this.id = id;
        this.halfedge = Optional.<IHalfEdge>empty();
    }

    private Face(int id, Optional<IHalfEdge> halfedge) {
        this.id = id;
        this.halfedge = halfedge;
    }

    void setHalfEdge(IHalfEdge halfedge) {
        this.halfedge = Optional.<IHalfEdge>of(halfedge);
    }

    void removeHalfEdge() {
        this.halfedge = Optional.<IHalfEdge>empty();
    }

    Face copy() {
        return new Face(this.id, this.halfedge);
    }

    int getId() {
        return this.id;
    }

    public Optional<IHalfEdge> getHalfEdge() {
        return this.halfedge;
    }

    @Override
    public String toString() {
        IHalfEdge edge = () -> -1;
        return String.format("Face %d: Edge %d", this.id,
                this.halfedge.orElse(edge).getId());
    }
}
