import java.util.Optional;

class Face {
    private final int id;
    private Optional<IHalfEdge> halfedge;

    Face(int id) {
        this.id = id;
        this.halfedge = Optional.<IHalfEdge>empty();
    }

    void setHalfEdge(IHalfEdge halfedge) {
        this.halfedge = Optional.<IHalfEdge>of(halfedge);
    }

    void removeHalfEdge() {
        this.halfedge = Optional.<IHalfEdge>empty();
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
