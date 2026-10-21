package matrixalgo;

public class EquationChain {
    private final Algorithms algorithms;
    private final Matrix target;

    public EquationChain(Algorithms algorithms, Matrix target) {
        this.algorithms = algorithms;
        this.target = target;
    }

    public EquationChain add(Matrix A, Matrix B) {
        algorithms.addMatrices(target, A, B);
        return this;
    }

    public EquationChain add(Matrix A) {
        algorithms.addMatrices(target, target, A);
        return this;
    }

    public EquationChain sub(Matrix A, Matrix B) {
        algorithms.subtractMatrices(target, A, B);
        return this;
    }

    public EquationChain sub(Matrix A) {
        algorithms.subtractMatrices(target, target, A);
        return this;
    }
}
