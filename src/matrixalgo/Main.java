package matrixalgo;

public class Main {
    public static void main(String[] args) {
        Matrix m1 = Matrix.generateRandom(6, 6);
        Matrix m2 = Matrix.generateRandom(6, 6);

        System.out.println(m1);
        System.out.println(m2);

        Matrix classic = new Matrix(6, 6);
        Matrix laderman = new Matrix(6, 6);

        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        algorithms.multiplyClassic(classic, m1, m2);
        System.out.println(classic);
        System.out.println(counter);

        counter.reset();
        algorithms.multiplyLaderman(laderman, m1, m2);
        System.out.println(laderman);
        System.out.println(counter);

        System.out.println(classic.equalsFuzzy(laderman, 1e-8));
    }
}
