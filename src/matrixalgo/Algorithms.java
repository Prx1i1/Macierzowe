package matrixalgo;

public class Algorithms {
    private final OpCounter counter;

    public Algorithms(OpCounter counter) {
        this.counter = counter;
    }

    public void addMatrices(Matrix target, Matrix op1, Matrix op2) {
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                target.put(y, x, op1.at(y, x) + op2.at(y, x));
                counter.additive++;
            }
        }
    }

    public void subtractMatrices(Matrix target, Matrix op1, Matrix op2) {
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                target.put(y, x, op1.at(y, x) - op2.at(y, x));
                counter.additive++;
            }
        }
    }

    public void multiplyClassic(Matrix target, Matrix op1, Matrix op2) {
        // here would go checks that the matrix sizes are correct, alas i dont feel like writing those
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                double sum = 0.0;
                for (int k = 0; k < op1.getCols(); k++) {
                    sum += op1.at(y, k) * op2.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, x, sum);
            }
        }
    }

    public void multiplyBinet(Matrix target, Matrix op1, Matrix op2, boolean unitAdd) {
        // again, checks go here
        if (target.getRows() == 1) {
            if (unitAdd) {
                target.addTo(0, 0, op1.at(0, 0) * op2.at(0, 0));
                counter.multiplicative++;
                counter.additive++;
            } else {
                target.put(0, 0, op1.at(0, 0) * op2.at(0, 0));
                counter.multiplicative++;
            }
            return;
        }

        int n = target.getRows();

        // if size is odd, cut off the edges, and recursively multiply
        // then add the missing values from the edges
        if (n % 2 == 1) {
            int m = n - 1;
            Matrix target_ul = target.view(0, m, 0, m);
            Matrix op1_ul = op1.view(0, m, 0, m);
            Matrix op2_ul = op2.view(0, m, 0, m);
            multiplyBinet(target_ul, op1_ul, op2_ul, unitAdd);

            // inside of the cut off region
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < m; x++) {
                    target.addTo(y, x, op1.at(y, m) * op2.at(m, x));
                    counter.multiplicative++;
                    counter.additive++;
                }
            }

            // bottom side
            for (int x = 0; x < m; x++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(m, k) * op2.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                if (unitAdd) {
                    target.addTo(m, x, sum);
                    counter.additive++;
                } else {
                    target.put(m, x, sum);
                }
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(y, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                if (unitAdd) {
                    target.addTo(y, m, sum);
                    counter.additive++;
                } else {
                    target.put(y, m, sum);
                }
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(m, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                if (unitAdd) {
                    target.addTo(m, m, sum);
                    counter.additive++;
                } else {
                    target.put(m, m, sum);
                }
            }

            return;
        }

        int half = target.getRows() / 2;

        Matrix target_11 = target.view(0, half, 0, half);
        Matrix target_12 = target.view(0, half, half, n);
        Matrix target_21 = target.view(half, n, 0, half);
        Matrix target_22 = target.view(half, n, half, n);

        Matrix op1_11 = op1.view(0, half, 0, half);
        Matrix op1_12 = op1.view(0, half, half, n);
        Matrix op1_21 = op1.view(half, n, 0, half);
        Matrix op1_22 = op1.view(half, n, half, n);

        Matrix op2_11 = op2.view(0, half, 0, half);
        Matrix op2_12 = op2.view(0, half, half, n);
        Matrix op2_21 = op2.view(half, n, 0, half);
        Matrix op2_22 = op2.view(half, n, half, n);

        multiplyBinet(target_11, op1_11, op2_11, unitAdd);
        multiplyBinet(target_11, op1_12, op2_21, true);

        multiplyBinet(target_12, op1_11, op2_12, unitAdd);
        multiplyBinet(target_12, op1_12, op2_22, true);

        multiplyBinet(target_21, op1_21, op2_11, unitAdd);
        multiplyBinet(target_21, op1_22, op2_21, true);

        multiplyBinet(target_22, op1_21, op2_12, unitAdd);
        multiplyBinet(target_22, op1_22, op2_22, true);
    }

    public void multiplyStrassen(Matrix target, Matrix A, Matrix B) {
        // again, checks go here
        if (target.getRows() == 1) {
            target.put(0, 0, A.at(0, 0) * B.at(0, 0));
            counter.multiplicative++;
            return;
        }

        int n = target.getRows();

        // if size is odd, cut off the edges, and recursively multiply
        // then add the missing values from the edges
        if (n % 2 == 1) {
            int m = n - 1;
            Matrix target_ul = target.view(0, m, 0, m);
            Matrix A_ul = A.view(0, m, 0, m);
            Matrix B_ul = B.view(0, m, 0, m);
            multiplyStrassen(target_ul, A_ul, B_ul);

            // inside of the cut off region
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < m; x++) {
                    target.addTo(y, x, A.at(y, m) * B.at(m, x));
                    counter.multiplicative++;
                    counter.additive++;
                }
            }

            // bottom side
            for (int x = 0; x < m; x++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, x, sum);
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(y, k) * B.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, m, sum);
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, m, sum);
            }

            return;
        }

        int half = target.getRows() / 2;

        Matrix target_11 = target.view(0, half, 0, half);
        Matrix target_12 = target.view(0, half, half, n);
        Matrix target_21 = target.view(half, n, 0, half);
        Matrix target_22 = target.view(half, n, half, n);

        Matrix A_11 = A.view(0, half, 0, half);
        Matrix A_12 = A.view(0, half, half, n);
        Matrix A_21 = A.view(half, n, 0, half);
        Matrix A_22 = A.view(half, n, half, n);

        Matrix B_11 = B.view(0, half, 0, half);
        Matrix B_12 = B.view(0, half, half, n);
        Matrix B_21 = B.view(half, n, 0, half);
        Matrix B_22 = B.view(half, n, half, n);

        Matrix scratch = new Matrix(half, half);
        Matrix scratch2 = new Matrix(half, half);

        Matrix P1 = new Matrix(half, half);
        addMatrices(scratch, A_11, A_22);
        addMatrices(scratch2, B_11, B_22);
        multiplyStrassen(P1, scratch, scratch2);

        Matrix P2 = new Matrix(half, half);
        addMatrices(scratch, A_21, A_22);
        multiplyStrassen(P2, scratch, B_11);

        Matrix P3 = new Matrix(half, half);
        subtractMatrices(scratch, B_12, B_22);
        multiplyStrassen(P3, A_11, scratch);

        Matrix P4 = new Matrix(half, half);
        subtractMatrices(scratch, B_21, B_11);
        multiplyStrassen(P4, A_22, scratch);

        Matrix P5 = new Matrix(half, half);
        addMatrices(scratch, A_11, A_12);
        multiplyStrassen(P5, scratch, B_22);

        Matrix P6 = new Matrix(half, half);
        subtractMatrices(scratch, A_21, A_11);
        addMatrices(scratch2, B_11, B_12);
        multiplyStrassen(P6, scratch, scratch2);

        Matrix P7 = new Matrix(half, half);
        subtractMatrices(scratch, A_12, A_22);
        addMatrices(scratch2, B_21, B_22);
        multiplyStrassen(P7, scratch, scratch2);

        addMatrices(target_11, P1, P4);
        subtractMatrices(target_11, target_11, P5);
        addMatrices(target_11, target_11, P7);

        addMatrices(target_12, P3, P5);

        addMatrices(target_21, P2, P4);

        subtractMatrices(target_22, P1, P2);
        addMatrices(target_22, target_22, P3);
        addMatrices(target_22, target_22, P6);
    }

    public void multiplyLaderman(Matrix target, Matrix A, Matrix B) {
        // again, checks go here
        if (target.getRows() == 1) {
            target.put(0, 0, A.at(0, 0) * B.at(0, 0));
            counter.multiplicative++;
            return;
        }

        int n = target.getRows();

        // if size is not a multiple of 3, cut off the edges, and recursively multiply
        // then add the missing values from the edges
        if (n % 3 != 0) {
            int m = n - 1;
            Matrix target_ul = target.view(0, m, 0, m);
            Matrix A_ul = A.view(0, m, 0, m);
            Matrix B_ul = B.view(0, m, 0, m);
            multiplyLaderman(target_ul, A_ul, B_ul);

            // inside of the cut off region
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < m; x++) {
                    target.addTo(y, x, A.at(y, m) * B.at(m, x));
                    counter.multiplicative++;
                    counter.additive++;
                }
            }

            // bottom side
            for (int x = 0; x < m; x++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, x, sum);
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(y, k) * B.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, m, sum);
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, m, sum);
            }

            return;
        }

        int third = target.getRows() / 3;

        Matrix[][] Tc = target.chunk(3, 3);
        Matrix[][] Ac = A.chunk(3, 3);
        Matrix[][] Bc = B.chunk(3, 3);

        Matrix scratch = new Matrix(third, third);
        Matrix scratch2 = new Matrix(third, third);

        Matrix m1 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[0][0], Ac[0][1])
                .add(Ac[0][2])
                .sub(Ac[1][0])
                .sub(Ac[1][1])
                .sub(Ac[2][1])
                .sub(Ac[2][2]);
        multiplyLaderman(m1, scratch, Bc[1][1]);

        Matrix m2 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[0][0], Ac[1][0]);
        new EquationChain(this, scratch2)
                .sub(Bc[1][1], Bc[0][1]);
        multiplyLaderman(m2, scratch, scratch2);

        Matrix m3 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Bc[0][1], Bc[0][0])
                .add(Bc[1][0])
                .sub(Bc[1][1])
                .sub(Bc[1][2])
                .sub(Bc[2][0])
                .add(Bc[2][2]);
        multiplyLaderman(m3, Ac[1][1], scratch);

        Matrix m4 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[1][0], Ac[0][0])
                .add(Ac[1][1]);
        new EquationChain(this, scratch2)
                .sub(Bc[0][0], Bc[0][1])
                .add(Bc[1][1]);
        multiplyLaderman(m4, scratch, scratch2);

        Matrix m5 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[1][0], Ac[1][1]);
        new EquationChain(this, scratch2)
                .sub(Bc[0][1], Bc[0][0]);
        multiplyLaderman(m5, scratch, scratch2);

        Matrix m6 = new Matrix(third, third);
        multiplyLaderman(m6, Ac[0][0], Bc[0][0]);

        Matrix m7 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[2][0], Ac[0][0])
                .add(Ac[2][1]);
        new EquationChain(this, scratch2)
                .sub(Bc[0][0], Bc[0][2])
                .add(Bc[1][2]);
        multiplyLaderman(m7, scratch, scratch2);

        Matrix m8 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[2][0], Ac[0][0]);
        new EquationChain(this, scratch2)
                .sub(Bc[0][2], Bc[1][2]);
        multiplyLaderman(m8, scratch, scratch2);

        Matrix m9 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[2][0], Ac[2][1]);
        new EquationChain(this, scratch2)
                .sub(Bc[0][2], Bc[0][0]);
        multiplyLaderman(m9, scratch, scratch2);

        Matrix m10 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[0][0], Ac[0][1])
                .add(Ac[0][2])
                .sub(Ac[1][1])
                .sub(Ac[1][2])
                .sub(Ac[2][0])
                .sub(Ac[2][1]);
        multiplyLaderman(m10, scratch, Bc[1][2]);

        Matrix m11 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Bc[0][2], Bc[0][0])
                .add(Bc[1][0])
                .sub(Bc[1][1])
                .sub(Bc[1][2])
                .sub(Bc[2][0])
                .add(Bc[2][1]);
        multiplyLaderman(m11, Ac[2][1], scratch);

        Matrix m12 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[2][1], Ac[0][2])
                .add(Ac[2][2]);
        new EquationChain(this, scratch2)
                .add(Bc[1][1], Bc[2][0])
                .sub(Bc[2][1]);
        multiplyLaderman(m12, scratch, scratch2);

        Matrix m13 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[0][2], Ac[2][2]);
        new EquationChain(this, scratch2)
                .sub(Bc[1][1], Bc[2][1]);
        multiplyLaderman(m13, scratch, scratch2);

        Matrix m14 = new Matrix(third, third);
        multiplyLaderman(m14, Ac[0][2], Bc[2][0]);

        Matrix m15 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[2][1], Ac[2][2]);
        new EquationChain(this, scratch2)
                .sub(Bc[2][1], Bc[2][0]);
        multiplyLaderman(m15, scratch, scratch2);

        Matrix m16 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[1][1], Ac[0][2])
                .add(Ac[1][2]);
        new EquationChain(this, scratch2)
                .add(Bc[1][2], Bc[2][0])
                .sub(Bc[2][2]);
        multiplyLaderman(m16, scratch, scratch2);

        Matrix m17 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .sub(Ac[0][2], Ac[1][2]);
        new EquationChain(this, scratch2)
                .sub(Bc[1][2], Bc[2][2]);
        multiplyLaderman(m17, scratch, scratch2);

        Matrix m18 = new Matrix(third, third);
        new EquationChain(this, scratch)
                .add(Ac[1][1], Ac[1][2]);
        new EquationChain(this, scratch2)
                .sub(Bc[2][2], Bc[2][0]);
        multiplyLaderman(m18, scratch, scratch2);

        Matrix m19 = new Matrix(third, third);
        multiplyLaderman(m19, Ac[0][1], Bc[1][0]);

        Matrix m20 = new Matrix(third, third);
        multiplyLaderman(m20, Ac[1][2], Bc[2][1]);

        Matrix m21 = new Matrix(third, third);
        multiplyLaderman(m21, Ac[1][0], Bc[0][2]);

        Matrix m22 = new Matrix(third, third);
        multiplyLaderman(m22, Ac[2][0], Bc[0][1]);

        Matrix m23 = new Matrix(third, third);
        multiplyLaderman(m23, Ac[2][2], Bc[2][2]);

        new EquationChain(this, Tc[0][0])
                .add(m6, m14).add(m19);
        new EquationChain(this, Tc[0][1])
                .add(m1, m4).add(m5).add(m6).add(m12).add(m14).add(m15);
        new EquationChain(this, Tc[0][2])
                .add(m6, m7).add(m9).add(m10).add(m14).add(m16).add(m18);
        new EquationChain(this, Tc[1][0])
                .add(m2, m3).add(m4).add(m6).add(m14).add(m16).add(m17);
        new EquationChain(this, Tc[1][1])
                .add(m2, m4).add(m5).add(m6).add(m20);
        new EquationChain(this, Tc[1][2])
                .add(m14, m16).add(m17).add(m18).add(m21);
        new EquationChain(this, Tc[2][0])
                .add(m6, m7).add(m8).add(m11).add(m12).add(m13).add(m14);
        new EquationChain(this, Tc[2][1])
                .add(m12, m13).add(m14).add(m15).add(m22);
        new EquationChain(this, Tc[2][2])
                .add(m6, m7).add(m8).add(m9).add(m23);
    }
}
