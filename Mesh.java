import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.IndexOutOfBoundsException;

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

    private Mesh(ImList<Vertex> vertices, ImList<Face> faces, ImList<HalfEdge> edges) {
        this.vertices = vertices;
        this.faces = faces;
        this.edges = edges;
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

    public Mesh moveVertex(int i, Point p) {
        try {
            this.vertices.get(i);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("no such vertex found");
            return this;
        }
        Vertex v = this.vertices.get(i);
        Mesh mesh = new Mesh(this.vertices.set(i, v.move(p)),
                this.faces, this.edges).copy();
        try {
            mesh.check();
        } catch (Error e) {
            System.out.println("move creates an invalid mesh");
            System.out.println(e);
            return this;
        }
        return mesh;
    }

    public Mesh splitEdgeAddVertex(int edgeIndex, Point vertexPoint) {
        try {
            this.edges.get(edgeIndex);
        } catch (IndexOutOfBoundsException e) {
            return this;
        }
        Vertex newVertex = new Vertex(this.vertices.size(), vertexPoint);
        Mesh mesh = this.copy();
        HalfEdge newEdge1 = new HalfEdge(this.edges.size());
        HalfEdge newEdge2 = new HalfEdge(this.edges.size() + 1);
        HalfEdge oldEdge1 = mesh.edges.get(edgeIndex);
        HalfEdge oldEdge2 = oldEdge1.getTwin().get();
       
        newVertex.setHalfEdge(newEdge1);

        newEdge1.setNext(oldEdge1.getNext());
        newEdge1.setPrev(oldEdge1);
        newEdge1.setTwin(newEdge2);
        newEdge1.setVertex(newVertex);
        if (oldEdge1.getFace().isPresent()) {
            newEdge1.setFace(oldEdge1.getFace().get());
        }

        newEdge2.setPrev(oldEdge2.getPrev());
        newEdge2.setNext(oldEdge2);
        newEdge2.setTwin(newEdge1);
        newEdge2.setVertex(oldEdge2.getVertex().get());
        if (oldEdge2.getFace().isPresent()) {
            newEdge2.setFace(oldEdge2.getFace().get());
        }
        
        oldEdge1.getNext().get().setPrev(newEdge1);
        oldEdge1.setNext(newEdge1);
        oldEdge2.getPrev().get().setNext(newEdge2);
        oldEdge2.setPrev(newEdge2);
        oldEdge2.getVertex().get().setHalfEdge(newEdge2);
        oldEdge2.setVertex(newVertex);
        mesh = new Mesh(mesh.vertices.add(newVertex), mesh.faces,
                mesh.edges.addAll(List.of(newEdge1, newEdge2)));
        mesh.check();
        return mesh;
    }

    public Mesh copy() {
        ImList<Vertex> vertices = new ImList<Vertex>();
        ImList<Face> faces = new ImList<Face>();
        ImList<HalfEdge> edges = new ImList<HalfEdge>();
        for (Vertex v : this.vertices) {
            vertices = vertices.add(v.copy());
        }
        for (Face f : this.faces) {
            faces = faces.add(f.copy());
        }
        for (HalfEdge e: this.edges) {
            edges = edges.add(e.copy());
        }
        Mesh mesh = new Mesh(vertices, faces, edges);
        
        for (Vertex v : this.vertices) {
            int i = v.getId();
            mesh.vertices.get(i).setHalfEdge(mesh.edges.get(((HalfEdge)v.getHalfEdge().get()).getId()));
        }
        for (Face f : this.faces) {
            int i = f.getId();
            mesh.faces.get(i).setHalfEdge(mesh.edges.get(((HalfEdge)f.getHalfEdge().get()).getId()));
        }
        for (HalfEdge e : this.edges) {
            HalfEdge he = mesh.edges.get(e.getId());
            he.setVertex(mesh.vertices.get(e.getVertex().get().getId()));
            he.setTwin(mesh.edges.get(e.getTwin().get().getId()));
            if (e.getFace().isPresent()) {
                he.setFace(mesh.faces.get(e.getFace().get().getId()));
            }
            he.setNext(mesh.edges.get(e.getNext().get().getId()));
            he.setPrev(mesh.edges.get(e.getPrev().get().getId()));
        }
        return mesh;
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
