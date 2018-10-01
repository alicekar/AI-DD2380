//package hiddenmarkovmodel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alexhermansson
 */
public class Matrix {
    
    public static double[] lineToArray(String[] string) {
        // Function taking a string, turning it into an array    
        double[] doubleArray = new double[string.length];
        for (int i = 0; i < string.length; i++) {
            String numberAsString = string[i];
            doubleArray[i] = Double.parseDouble(numberAsString);
        }
        
        return doubleArray;
    }
    
    public static double[][] createMatrix(double[] vector) {
        /* Function taking a "vector" of form [N M elem00 elem01 ... elemNM]
           making it into an NxM matrix */
        int N = (int)vector[0];
        int M = (int)vector[1];
        double[][] matrix = new double[N][M];
        int i = 2;
        while(i < vector.length){
            for(int row = 0; row < N; row++){
                for(int col = 0; col < M; col++){
                    matrix[row][col] = vector[i];
                    i++;
                }
            }
        }
        return matrix;
    }
    
    public static void printMatrix(double[][] matrix) {
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
            
        }
        System.out.println();
        
    }
    
    public static double[][] transpose(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        double[][] b = new double[n][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                b[j][i] = a[i][j];
        return b;
    }

    // return c = a * b
    public static double[][] multiply(double[][] a, double[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] c = new double[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    // matrix-vector multiplication (y = A * x)
    public static double[] multiply(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += a[i][j] * x[j];
        return y;
    }


    // vector-matrix multiplication (y = x^T A)
    public static double[] multiply(double[] x, double[][] a) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != m) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[n];
        for (int j = 0; j < n; j++)
            for (int i = 0; i < m; i++)
                y[j] += a[i][j] * x[i];
        return y;
    }

    public static double[][] elemMult(double[][] x, double[][] y) {
        int n = x.length;
        int m = x[0].length;
        if (n != y.length || m != y[0].length) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] z = new double[n][m];
        for (int j = 0; j < n; j++){
            for ( int i = 0; i < m; i++) {
                z[i][j] = x[i][j]*y[i][j];
            }
        }
        return z; 
    }
    
}
