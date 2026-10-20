package matrixalgo;

public class OpCounter {
    public int additive = 0;
    public int multiplicative = 0;

    public void reset() {
        additive = 0;
        multiplicative = 0;
    }

    @Override
    public String toString() {
        return "OpCounter{" +
                "additive=" + additive +
                ", multiplicative=" + multiplicative +
                '}';
    }
}
