import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class MeshVisualiser {
    public MeshVisualiser(Mesh m) {
        m.check();
        String output = "";
        ImList<Face> faces = new ImList<Face>(m.getFaces());
        for (Face f : faces) {
            HalfEdge start = (HalfEdge) f.getHalfEdge().get();
            HalfEdge curr = start;
            do {
                Point p = curr.getVertex().get().getPoint();
                output += p.getX() + "," + p.getY() + " ";
                curr = curr.getNext().get();
            } while (!start.equals(curr));
            output += "\n";
        }
        
        try {
            File file = new File("mesh.txt");
            FileWriter fileWriter = new FileWriter(file);
            file.createNewFile();
            fileWriter.write(output);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error! Could not write output");
        }
        return;
    }
}
