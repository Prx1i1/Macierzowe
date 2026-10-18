mod matrix;
mod binet;
use matrix::{Matrix, generateRandomMatrix};
use binet::multiplyBinet;

fn main() {
    let a: Matrix = generateRandomMatrix(3);
    let b: Matrix = generateRandomMatrix(3);

    let c = multiplyBinet(&a.data, &b.data);

    println!("Wynik:");
    for row in c {
        println!("{:?}", row);
    }
}
