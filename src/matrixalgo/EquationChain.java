package matrixalgo;

public class EquationChain {
    private final Algorithms algorithms;
    private final Matrix target;

    public EquationChain(Algorithms algorithms, Matrix target) {
        this.algorithms = algorithms;
        this.target = target;
    }

    /**
     * Adds matrix A and B, with the result stored in target
     * <p>
     * Equivalent to target = A + B, except no intermediary allocation is created,
     * result is saved directly to target, and current values in target are ignored and overridden
     * <p>
     * You'll likely want to use this variant as the first operation in the chain
     */
    public EquationChain add(Matrix A, Matrix B) {
        algorithms.addMatrices(target, A, B);
        return this;
    }

    /**
     * Adds matrix A to the values stored in target
     * Equivalent to target += A
     * <p>
     * You'll likely want to use this variant after an initial call to an operation with 2 arguments
     */
    public EquationChain add(Matrix A) {
        algorithms.addMatrices(target, target, A);
        return this;
    }

    /**
     * Subtracts matrix A and B, with the result stored in target
     * <p>
     * Equivalent to target = A - B, except no intermediary allocation is created,
     * result is saved directly to target, and current values in target are ignored and overridden
     * <p>
     * You'll likely want to use this variant as the first operation in the chain
     */
    public EquationChain sub(Matrix A, Matrix B) {
        algorithms.subtractMatrices(target, A, B);
        return this;
    }

    /**
     * Subtracts matrix A from the values stored in target
     * Equivalent to target -= A
     * <p>
     * You'll likely want to use this variant after an initial call to an operation with 2 arguments
     */
    public EquationChain sub(Matrix A) {
        algorithms.subtractMatrices(target, target, A);
        return this;
    }
}
