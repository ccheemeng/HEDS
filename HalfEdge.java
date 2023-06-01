import java.util.Optional;

class HalfEdge {
    private final int id;
    private Optional<Vertex> v;
    private Optional<Face> f;
    private Optional<HalfEdge> twin;
    private Optional<HalfEdge> next;
    private Optional<HalfEdge> prev;

    HalfEdge(int id) {
        this(id, Optional.<Vertex>empty(), Optional.<Face>empty(),
                Optional.<HalfEdge>empty(), Optional.<HalfEdge>empty(), Optional.<HalfEdge>empty());
    }

    private HalfEdge(int id, Optional<Vertex> v, Optional<Face> f,
            Optional<HalfEdge> twin, Optional<HalfEdge> next, Optional<HalfEdge> prev) {
        this.id = id;
        this.v = v;
        this.f = f;
        this.twin = twin;
        this.next = next;
        this.prev = prev;
    }

    void setVertex(Vertex v) {
        this.v = Optional.<Vertex>of(v);
    }

    void setFace(Face f) {
        this.f = Optional.<Face>of(f);
    }

    void setTwin(HalfEdge twin) {
        this.twin = Optional.<HalfEdge>of(twin);
    }

    void setNext(HalfEdge next) {
        this.next = Optional.<HalfEdge>of(next);
    }

    void setNext(Optional<HalfEdge> next) {
        this.next = next;
    }

    void setPrev(HalfEdge prev) {
        this.prev = Optional.<HalfEdge>of(prev);
    }

    void setPrev(Optional<HalfEdge> prev) {
        this.prev = prev;
    }

    HalfEdge copy() {
        return new HalfEdge(this.id, this.v, this.f,
                this.twin, this.next, this.prev);
    }

    public int getId() {
        return this.id;
    }

    Optional<Vertex> getVertex() {
        return this.v;
    }

    Optional<Face> getFace() {
        return this.f;
    }

    Optional<HalfEdge> getTwin() {
        return this.twin;
    }

    Optional<HalfEdge> getNext() {
        return this.next;
    }

    Optional<HalfEdge> getPrev() {
        return this.prev;
    }

    double angle() {
        if (this.next.isEmpty() || this.v.isEmpty() ||
                this.next.get().v.isEmpty()) {
            return 2 * Math.PI;
        }
        Vertex v1 = this.v.get();
        Vertex v2 = this.next.get().v.get();
        return v2.translateAngle(v1.angle() + Math.PI,
                v1.distanceFromOrigin()).angle();
    }

    double angleBetween(HalfEdge other) {
        if (this.next.isEmpty() || this.v.isEmpty() ||
                this.next.get().v.isEmpty() ||
                other.next.isEmpty() || other.v.isEmpty() ||
                other.next.get().v.isEmpty()) {
            return 2 * Math.PI;
        }
        return other.angle() - this.angle();
    }

    double length() {
        if (this.next.isEmpty() || this.v.isEmpty() ||
                this.next.get().v.isEmpty()) {
            return -1.0;
        }
        return this.v.get().distanceBetween(
                this.next.get().v.get());
    }

    Point midpoint() {
        if (this.next.isEmpty() || this.next.get().v.isEmpty()) {
            if (this.v.isEmpty()) {
                return Point.origin();
            }
            return this.v.get().getPoint();
        }
        return this.v.get().midpoint(
                this.next.get().v.get());
    }

    Point lerp(double ratio) {
        if (this.next.isEmpty() || this.next.get().v.isEmpty()) {
            if (this.v.isEmpty()) {
                return Point.origin();
            }
            return this.v.get().getPoint();
        }
        return this.v.get().lerp(
                this.next.get().v.get(), ratio);
    }

    @Override
    public String toString() {
        String output = "Edge " + this.id + ": " +
            "Vertex " + (this.v.isPresent() ? this.v.get().getId()  + "" : "") + ", " +
            "Face " + (this.f.isPresent() ? this.f.get().getId() + "" : "") + ", " +
            "Twin " + (this.twin.isPresent() ? this.twin.get().getId() + "" : "") + ", " +
            "Next " + (this.next.isPresent() ? this.next.get().getId() + "" : "") + ", " +
            "Prev " + (this.prev.isPresent() ? this.prev.get().getId() + "" : "");
        return output;
    }
}
