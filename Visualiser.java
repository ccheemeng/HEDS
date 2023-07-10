import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileNotFoundException;

class Visualiser {
    public static void visualise(Mesh m) {
        m.check();
        String output = "";
        ImList<Face> faces = m.getFaces();
        for (Face f : faces) {
            HalfEdge start = f.getHalfEdge().get();
            HalfEdge curr = start;
            do {
                Point p = curr.getVertex().get().getPoint();
                output += p.getX() + "," + p.getY() + " ";
                curr = curr.getNext().get();
            } while (!start.equals(curr));
            output += "\n";
        }        
        try {
            File file = new File("mesh.out");
            FileWriter fileWriter = new FileWriter(file);
            file.createNewFile();
            fileWriter.write(output);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error! Could not write output");
        }
    }

    public static Mesh create() {
        ImList<Point> points = new ImList<Point>();
        try {
            Scanner sc = new Scanner(new File("mesh.in"));
            int numOfVertices = sc.nextInt();
            System.out.println(numOfVertices);
            for (int i = 0; i < numOfVertices; ++i) {
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                Point p = new Point(x, y);
                points = points.add(p);
            }
        } catch (FileNotFoundException e) {}
        ImList<Integer> faceVertices = new ImList<Integer>();
        for (int i = 0; i < points.size(); ++i) {
            faceVertices = faceVertices.add(i);
        }
        Mesh m = new Mesh(points, new ImList<ImList<Integer>>().add(faceVertices));
        Visualiser.visualise(m);
        return m;
    }
}
