package matrixalgo;

public class Main {
    public static void main(String[] args) {
        Matrix m = new Matrix(6, 6);
        int i = 0;
        for (int y = 0; y < m.getRows(); y++) {
            for (int x = 0; x < m.getCols(); x++) {
                m.put(y, x, i++);
            }
        }

        Matrix m2 = new Matrix(6, 6);
        for (int y = 0; y < m2.getRows(); y++) {
            for (int x = 0; x < m2.getCols(); x++) {
                m2.put(y, x, i++);
            }
        }

        System.out.println(m);
        System.out.println(m2);

        Matrix classic = new Matrix(6, 6);
        Matrix laderman = new Matrix(6, 6);

        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        algorithms.multiplyClassic(classic, m, m2);
        System.out.println(classic);
        System.out.println(counter);

        counter.reset();
        algorithms.multiplyLaderman(laderman, m, m2);
        System.out.println(laderman);
        System.out.println(counter);

        System.out.println(classic.equals(laderman));
    }
}
