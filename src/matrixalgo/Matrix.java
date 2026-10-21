package matrixalgo;

import java.util.Arrays;

public class Matrix {
    private final double[] data;
    private final int offset;
    private final int rows;
    private final int cols;
    private final int stride;

    public Matrix(int rows, int cols) {
        int length = rows * cols;
        data = new double[length];
        offset = 0;
        this.rows = rows;
        this.cols = cols;
        this.stride = cols;
    }

    public Matrix(double[] data, int offset, int rows, int cols, int stride) {
        this.data = data;
        this.offset = offset;
        this.rows = rows;
        this.cols = cols;
        this.stride = stride;
    }

    public static Matrix generateRandom(int rows, int cols) {
        Matrix m = new Matrix(rows, cols);
        // we know the matrix is dense (stride == cols)
        for (int i = 0; i < m.data.length; i++) {
            m.data[i] = Util.random(1e-8, 1.0);
        }
        return m;
    }

    private Coords indexToCoords(int i) {
        int x = i % cols;
        int y = i / cols;
        return new Coords(y, x);
    }

    private int coordsToIndex(int y, int x) {
        if (y > rows) throw new IllegalArgumentException("row %d is out of bounds for matrix with %d rows".formatted(y, rows));
        if (x > cols) throw new IllegalArgumentException("col %d is out of bounds for matrix with %d cols".formatted(x, cols));
        return offset + y * stride + x;
    }

    /**
     * Retrieve the value in the matrix at the given coordinates
     * @param y the row of the matrix
     * @param x the column of the matrix
     * @return the value from the data array as if by data[y][x]
     */
    public double at(int y, int x) {
        return data[coordsToIndex(y, x)];
    }

    /**
     * Set the value in the matrix at the given coordinates
     * @param y the row of the matrix
     * @param x the column of the matrix
     * @param value the new value for the cell
     */
    public void put(int y, int x, double value) {
        data[coordsToIndex(y, x)] = value;
    }

    /**
     * Adds the value to the matrix at the given coordinates
     * @param y the row of the matrix
     * @param x the column of the matrix
     * @param value the new value for the cell
     */
    public void addTo(int y, int x, double value) {
        data[coordsToIndex(y, x)] += value;
    }

    /**
     * Creates a view of the matrix, backed by the same array
     * @param rowStart first row seen in the view, inclusive
     * @param rowEnd last row seen in the view, exclusive
     * @param colStart first col seen in the view, inclusive
     * @param colEnd last col seen in the view, exclusive
     * @return a new matrix object, looking at a region of the original matrix
     */
    public Matrix view(int rowStart, int rowEnd, int colStart, int colEnd) {
        int newRows = rowEnd - rowStart;
        int newCols = colEnd - colStart;
        int startOffset = coordsToIndex(rowStart, colStart);
        return new Matrix(data, startOffset, newRows, newCols, stride);
    }

    public Matrix[][] chunk(int rowChunks, int colChunks) {
        Matrix[][] chunks = new Matrix[rowChunks][colChunks];

        int rowLength = rows / rowChunks;
        int colLength = cols / colChunks;

        for (int y = 0; y < rowChunks; y++) {
            for (int x = 0; x < colChunks; x++) {
                chunks[y][x] = view(rowLength * y, rowLength * (y+1), colLength * x, colLength * (x+1));
            }
        }

        return chunks;
    }

    public void fill(double value) {
        if (stride == cols) {
            int length = rows * cols;
            Arrays.fill(data, offset, offset + length, value);
        } else {
            for (int y = 0; y < rows; y++) {
                int start = offset + y * stride;
                Arrays.fill(data, start, start + cols, value);
            }
        }
    }

    /**
     * Empty the matrix by setting each cell to 0.0
     */
    public void reset() {
        fill(0.0);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getStride() {
        return stride;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        Matrix o = (Matrix) obj;
        if (rows != o.rows || cols != o.cols) return false;

        if (rows == stride && o.rows == o.stride) {
            int length = rows * cols;
            return Arrays.equals(data, offset, length, o.data, o.offset, length);
        } else {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    if (at(y, x) != o.at(y, x)) return false;
                }
            }
        }
        return true;
    }

    public boolean equalsFuzzy(Matrix other, double epsilon) {
        if (other == null || getClass() != other.getClass()) return false;
        Matrix o = (Matrix) other;
        if (rows != o.rows || cols != o.cols) return false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (Math.abs(at(y, x) - o.at(y, x)) > epsilon) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String header = "Matrix %dx%d\n".formatted(rows, cols);
        StringBuilder contents = new StringBuilder();
        for (int y = 0; y < rows; y++) {
            contents.append("| ");
            for (int x = 0; x < cols; x++) {
                contents.append("%10.4f ".formatted(at(y, x)));
            }
            contents.append("|\n");
        }
        return header + contents;
    }

    public record Coords(int y, int x) {}
}
