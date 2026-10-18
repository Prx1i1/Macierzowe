pub fn multiplyBinet(a: &[Vec<f64>], b: &[Vec<f64>]) -> Vec<Vec<f64>> {
    let n = a.len();

    assert!(n > 0 && b.len() == n, "Nieprawidłowe wymiary macierzy");
    assert!(a.iter().all(|row| row.len() == n));
    assert!(b.iter().all(|row| row.len() == n));

    // Przypadek bazowy
    if n == 1 {
        return vec![vec![a[0][0] * b[0][0]]];
    }

    let mut c = vec![vec![0.0; n]; n];

    // DYNAMIC PEELING: nieparzysty rozmiar
    if n % 2 == 1 {
        let m = n - 1;

        // Mnożymy lewy górny blok (n-1) x (n-1)
        let a00: Vec<Vec<f64>> =
            a[..m].iter().map(|row| row[..m].to_vec()).collect();

        let b00: Vec<Vec<f64>> =
            b[..m].iter().map(|row| row[..m].to_vec()).collect();

        let mut c00 = multiplyBinet(&a00, &b00);

        // Uwzględniamy ostatni składnik sumy:
        // C[i][j] += A[i][m] * B[m][j]
        for i in 0..m {
            for j in 0..m {
                c00[i][j] += a[i][m] * b[m][j];
            }
        }

        // Przepisujemy wynik do macierzy C
        for i in 0..m {
            for j in 0..m {
                c[i][j] = c00[i][j];
            }
        }

        // Ostatnia kolumna
        for i in 0..m {
            for k in 0..m {
                c[i][m] += a[i][k] * b[k][m];
            }
            c[i][m] += a[i][m] * b[m][m];
        }

        // Ostatni wiersz
        for j in 0..m {
            for k in 0..m {
                c[m][j] += a[m][k] * b[k][j];
            }
            c[m][j] += a[m][m] * b[m][j];
        }

        // Prawy dolny element
        for k in 0..n {
            c[m][m] += a[m][k] * b[k][m];
        }

        return c;
    }

    // Parzysty rozmiar: dzielimy macierze na cztery bloki
    let half = n / 2;

    let a11: Vec<Vec<f64>> = a[..half]
        .iter().map(|row| row[..half].to_vec()).collect();
    let a12: Vec<Vec<f64>> = a[..half]
        .iter().map(|row| row[half..].to_vec()).collect();
    let a21: Vec<Vec<f64>> = a[half..]
        .iter().map(|row| row[..half].to_vec()).collect();
    let a22: Vec<Vec<f64>> = a[half..]
        .iter().map(|row| row[half..].to_vec()).collect();

    let b11: Vec<Vec<f64>> = b[..half]
        .iter().map(|row| row[..half].to_vec()).collect();
    let b12: Vec<Vec<f64>> = b[..half]
        .iter().map(|row| row[half..].to_vec()).collect();
    let b21: Vec<Vec<f64>> = b[half..]
        .iter().map(|row| row[..half].to_vec()).collect();
    let b22: Vec<Vec<f64>> = b[half..]
        .iter().map(|row| row[half..].to_vec()).collect();

    let m1 = multiplyBinet(&a11, &b11);
    let m2 = multiplyBinet(&a12, &b21);
    let m3 = multiplyBinet(&a11, &b12);
    let m4 = multiplyBinet(&a12, &b22);
    let m5 = multiplyBinet(&a21, &b11);
    let m6 = multiplyBinet(&a22, &b21);
    let m7 = multiplyBinet(&a21, &b12);
    let m8 = multiplyBinet(&a22, &b22);

    for i in 0..half {
        for j in 0..half {
            c[i][j] = m1[i][j] + m2[i][j];
            c[i][j + half] = m3[i][j] + m4[i][j];
            c[i + half][j] = m5[i][j] + m6[i][j];
            c[i + half][j + half] = m7[i][j] + m8[i][j];
        }
    }

    c
}

