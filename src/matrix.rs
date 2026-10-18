use rand::Rng;

pub struct Matrix {
    pub data: Vec<Vec<f64>>,
    pub rows: usize,
    pub cols: usize,
}

fn randomNumber(lower: f64, upper: f64) -> f64 {
    // Generate a random number between lower and upper
    lower + (upper - lower) * rand::random::<f64>()
}

pub fn generateRandomMatrix(n: usize) -> Matrix {
    let mut data = Vec::new();
    let rows = n;
    let cols = n;
    for _ in 0..rows {
        let row = vec![randomNumber(1e-8, 1.0); cols];
        data.push(row);
    }
    Matrix { data, rows, cols }
}

pub fn generateEmptyMatrix(n: usize) -> Matrix {
    let data = vec![vec![0.0; n]; n];
    Matrix { data, rows: n, cols: n }
}