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
        HalfEdge v2In = new HalfEdge(this.edges.size());
        HalfEdge v1In = new HalfEdge(this.edges.size() + 1);
        Mesh mesh = this.copy();
        HalfEdge v1Out = mesh.edges.get(edgeIndex);
        HalfEdge v2Out = v1Out.getTwin().get();
        Vertex v1 = v1Out.getVertex().get();
        Vertex v2 = v2Out.getVertex().get();

        newVertex.setHalfEdge(v2In);
        v1Out.getNext().get().setPrev(v2In);
        v2In.setNext(v1Out.getNext().get());
        v2Out.getNext().get().setPrev(v1In);
        v1In.setNext(v2Out.getNext().get());
        v1Out.setNext(v2In);
        v1Out.setTwin(v1In);
        v2Out.setNext(v1In);
        v2Out.setTwin(v2In);
        v2In.setVertex(newVertex);
        v2In.setPrev(v1Out);
        v2In.setTwin(v2Out);
        if (v1Out.getFace().isPresent()) {
            v2In.setFace(v1Out.getFace().get());
        }
        v1In.setVertex(newVertex);
        v1In.setPrev(v2Out);
        v1In.setTwin(v1Out);
        if (v2Out.getFace().isPresent()) {
            v1In.setFace(v2Out.getFace().get());
        }
    
        mesh = new Mesh(mesh.vertices.add(newVertex), mesh.faces,
                mesh.edges.addAll(List.of(v2In, v1In)));
        mesh.check();
        return mesh;
    }

    public Mesh splitFaceAddEdges(int faceIndex, int v1Index, int v2Index) {
        try {
            this.faces.get(faceIndex);
            getVerticesOnFace(faceIndex).get(v1Index);
            getVerticesOnFace(faceIndex).get(v2Index);
        } catch (IndexOutOfBoundsException e) {
            return this;
        }
        if (Math.abs(v1Index - v2Index) < 2 ||
                getVerticesOnFace(faceIndex).size() <= 3) {
            return this;
        }
        Face newFace = new Face(this.faces.size());
        HalfEdge newEdge1 = new HalfEdge(this.edges.size());
        HalfEdge newEdge2 = new HalfEdge(this.edges.size() + 1);
        Mesh mesh = this.copy();
        Vertex v1 = mesh.getVerticesOnFace(faceIndex).get(v1Index);
        Vertex v2 = mesh.getVerticesOnFace(faceIndex).get(v2Index);
        Face f = mesh.faces.get(faceIndex);
        HalfEdge v1Out = (HalfEdge) f.getHalfEdge().get();
        HalfEdge v1In = v1Out.getPrev().get();
        for (int i = 0; i < v1Index; ++i) {
            v1Out = v1Out.getNext().get();
            v1In = v1Out.getPrev().get();
        }
        HalfEdge v2Out = (HalfEdge) f.getHalfEdge().get();
        HalfEdge v2In = v2Out.getPrev().get();
        for (int i = 0; i < v2Index; ++i) {
            v2Out = v2Out.getNext().get();
            v2In = v2Out.getPrev().get();
        }
        Face oldFace = mesh.faces.get(faceIndex);
        
        oldFace.setHalfEdge(newEdge1);
        newFace.setHalfEdge(newEdge2);

        v1In.setNext(newEdge1);
        v1Out.setPrev(newEdge2);
        v1Out.setFace(newFace);
        v2In.setNext(newEdge2);
        v2In.setFace(newFace);
        v2Out.setPrev(newEdge1);
        newEdge1.setPrev(v1In);
        newEdge1.setNext(v2Out);
        newEdge1.setTwin(newEdge2);
        newEdge1.setVertex(v1);
        newEdge1.setFace(oldFace);
        newEdge2.setPrev(v2In);
        newEdge2.setNext(v1Out);
        newEdge2.setTwin(newEdge1);
        newEdge2.setVertex(v2);
        newEdge2.setFace(newFace);

        mesh = new Mesh(mesh.vertices, mesh.faces.add(newFace),
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

    public List<Vertex> getVerticesOnFace(int i) {
        try {
            this.faces.get(i);
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
        Face f = this.faces.get(i);
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        HalfEdge start = (HalfEdge) f.getHalfEdge().get();
        HalfEdge curr = start;
        do {
            vertices.add(curr.getVertex().get());
            curr = curr.getNext().get();
        } while (!start.equals(curr));
        return Collections.unmodifiableList(vertices);
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
