import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Derived from
 * @author enjmiah
 */
public class Mesh {
    private final ImList<Vertex> vertices;
    private final ImList<Face> faces;
    private final ImList<HalfEdge> edges;

    /**
     * Constructs a mesh based on the Wavefront OBJ format.
     * Note that vertices are indexed from 0.
     *
     * @param points a {@code List} of {@code Point} for each vertex in the mesh.
     * @param faces  a {@code List} of a {@code List} of integers for the indices of each vertex in each face.
     */
    public Mesh(List<? extends Point> points,
            List<? extends List<? extends Integer>> faces) {
        ImList<Vertex> newVertices = new ImList<Vertex>();
        ImList<Face> newFaces = new ImList<Face>();
        ImList<HalfEdge> newEdges = new ImList<HalfEdge>();
        ImMap<String, HalfEdge> newEdgeMap = new ImMap<String, HalfEdge>();

        for (int i = 0; i < points.size(); ++i) {
            Vertex v = new Vertex(i, points.get(i));
            newVertices = newVertices.add(v);
        }
        
        for (List<? extends Integer> f : faces) {
            ImList<HalfEdge> faceEdges = new ImList<HalfEdge>();
            for (int i = 0; i < f.size(); ++i) {
                int i2 = i < f.size() - 1 ? i + 1 : 0;
                HalfEdge edge = new HalfEdge(newEdges.size());
                Vertex v1 = newVertices.get(f.get(i));
                Vertex v2 = newVertices.get(f.get(i2));
                String key = v1.getId() + ";" + v2.getId();
                newEdgeMap = newEdgeMap.put(key, edge);
                edge.setVertex(v1);
                if (v1.getHalfEdge().isEmpty()) {
                    v1.setHalfEdge(edge);
                }
                Optional<HalfEdge> twin = newEdgeMap.get(
                        v2.getId() + ";" + v1.getId());
                if (twin.isPresent()) {
                    edge.setTwin(twin.get());
                    twin.get().setTwin(edge);
                }
                newEdges = newEdges.add(edge);
                faceEdges = faceEdges.add(edge);
            }
            
            Face face = new Face(newFaces.size());
            face.setHalfEdge(faceEdges.get(0));
            newFaces = newFaces.add(face);
            for (HalfEdge currEdge : faceEdges) {
                currEdge.setFace(face);
            }
            int len = faceEdges.size();
            for (int i = 0; i < len; ++i) {
                faceEdges.get(i).setNext(faceEdges.get((i + 1) % len));
                faceEdges.get(i).setPrev(faceEdges.get((i - 1 + len) % len));
            }
        }

        for (int i = 0, len = newEdges.size(); i < len; ++i) {
            HalfEdge edge = newEdges.get(i);
            if (edge.getTwin().isEmpty()) {
                HalfEdge newHalfEdge = new HalfEdge(newEdges.size());
                Vertex v1 = edge.getNext().get().getVertex().get();
                Vertex v2 = edge.getVertex().get();
                String key = v1.getId() + ";" + v2.getId();
                newEdgeMap = newEdgeMap.put(key, newHalfEdge);
                newHalfEdge.setVertex(v1);
                if (v1.getHalfEdge().isEmpty()) {
                    v1.setHalfEdge(newHalfEdge);
                }
                Optional<HalfEdge> twin = newEdgeMap.get(
                        v2.getId() + ";" + v1.getId());
                if (twin.isPresent()) {
                    newHalfEdge.setTwin(twin.get());
                    twin.get().setTwin(newHalfEdge);
                }
                newEdges = newEdges.add(newHalfEdge);
            }
        }

        for (HalfEdge edge : newEdges) {
            if (edge.getFace().isEmpty()) {
                Optional<HalfEdge> next = edge.getTwin();
                if (next.isPresent()) {
                    do {
                        Optional<HalfEdge> newNext = next.get().getPrev();
                        if (newNext.isPresent()) {
                            next = newNext.get().getTwin();
                        }
                    } while (next.get().getFace().isPresent());
                    edge.setNext(next.get());
                    next.get().setPrev(edge);
                }
            }
        }

        this.vertices = newVertices;
        this.faces = newFaces;
        this.edges = newEdges;
    }

    public void check() {
        for (HalfEdge e : this.edges) {
            if (e.getTwin().isPresent() &&
                    !e.equals(e.getTwin().get().getTwin().get())) {
                throw new Error("edge: twin inconsistent");
            }
            if (e.getNext().isPresent() &&
                    !e.getFace().equals(e.getNext().get().getFace())) {
                throw new Error("edge: next face inconsistent");
            }
            if (e.getPrev().isPresent() &&
                    !e.getFace().equals(e.getPrev().get().getFace())) {
                throw new Error("edge: prev face inconsistent");
            }
            if (e.getPrev().isPresent() &&
                    !e.equals(e.getPrev().get().getNext().get())) {
                throw new Error("edge: next inconsistent");
            }
            if (e.getNext().isPresent() &&
                    !e.equals(e.getNext().get().getPrev().get())) {
                throw new Error("edge: prev inconsistent");
            }
        }

        for (Vertex v : this.vertices) {
            if (v.getHalfEdge().isPresent() &&
                    !v.equals(((HalfEdge) v.getHalfEdge().get()).getVertex().get())) {
                throw new Error("vertex: edge inconsistent");
            }
        }

        for (Face f : this.faces) {
            if (f.getHalfEdge().isPresent() &&
                    !f.equals(((HalfEdge) f.getHalfEdge().get()).getFace().get())) {
                throw new Error("face: edge inconsistent");
            }
        }
    }

    public List<Vertex> getVertices() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (Vertex v : this.vertices) {
            vertices.add(v);
        }
        return Collections.unmodifiableList(vertices);
    }

    public List<Face> getFaces() {
        ArrayList<Face> faces = new ArrayList<Face>();
        for (Face f : this.faces) {
            faces.add(f);
        }
        return Collections.unmodifiableList(faces);
    }

    public List<HalfEdge> getHalfEdge() {
        ArrayList<HalfEdge> edges = new ArrayList<HalfEdge>();
        for (HalfEdge e : this.edges) {
            edges.add(e);
        }
        return Collections.unmodifiableList(edges);
    }

    @Override
    public String toString() {
        String output = "";
        for (Vertex v : this.vertices) {
            output += v.toString() + "\n";
        }
        for (Face f : this.faces) {
            output += f.toString() + "\n";
        }
        for (HalfEdge e : this.edges) {
            output += e.toString() + "\n";
        }
        return output;
    }
}
