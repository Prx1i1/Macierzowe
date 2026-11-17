package matrixalgo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Octave {
    private static PrintWriter writer;

    private static void save(String name, Matrix m) {
        writer.println("# name: " + name);
        writer.println("# type: matrix");
        writer.println("# rows: " + m.getRows());
        writer.println("# columns: " + m.getCols());
        for (int y = 0; y < m.getRows(); y++) {
            for (int x = 0; x < m.getCols(); x++) {
                writer.printf(" %.17f", m.at(y, x));
            }
            writer.println();
        }
        writer.println();
        writer.println();
        writer.flush();
    }

    public static void main(String[] args) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter("oktawawa")));

        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        Matrix m = Matrix.generateRandom(8, 8);
        save("mat", m);

        Matrix invertedBinet = new Matrix(m);
        algorithms.invertBinet(invertedBinet);
        save("javaInvertedBinet", invertedBinet);

        Matrix invertedStrassen = new Matrix(m);
        algorithms.invertStrassen(invertedStrassen);
        save("javaInvertedStrassen", invertedStrassen);

        Matrix invertedLaderman = m.like();
        algorithms.invertLaderman(invertedLaderman, m);
        save("javaInvertedLaderman", invertedLaderman);

        Matrix Lbinet = m.like();
        Matrix Ubinet = m.like();
        algorithms.luBinet(Lbinet, Ubinet, m);
        save("javaLbinet", Lbinet);
        save("javaUbinet", Ubinet);

        Matrix Lstrassen = m.like();
        Matrix Ustrassen = m.like();
        algorithms.luStrassen(Lstrassen, Ustrassen, m);
        save("javaLstrassen", Lstrassen);
        save("javaUstrassen", Ustrassen);
    }
}
