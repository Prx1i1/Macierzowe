package matrixalgo;

public class Main {
    public static void main(String[] args) {
        Matrix m1 = new Matrix(new double[] {
                9, 5, 4, 3,
                8, 2, 4, 4,
                3, 6, 5, 7,
                6, 9, 3, 5,
        }, 0, 4, 4, 4);

        Matrix m2 = new Matrix(new double[] {
                3, 6, 3, 1,
                2, 2, 6, 6,
                0, 1, 6, 7,
                3, 2, 2, 5,
        }, 0, 4, 4, 4);

        Matrix result = new Matrix(4, 4);

        Matrix m3 = new Matrix(new double[] {
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 10,
                11, 12, 13, 14, 15,
                16, 17, 18, 19, 20,
                21, 22, 23, 24, 25
        }, 0, 5, 5, 5);

        Matrix result5 = new Matrix(5, 5);

        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        algorithms.multiplyClassic(result5, m3, m3);
        System.out.println(result5);
        System.out.println(counter);

        result5 = new Matrix(5, 5);
        counter.reset();
        algorithms.multiplyBinet(result5, m3, m3);
        System.out.println(result5);
        System.out.println(counter);
    }
}
