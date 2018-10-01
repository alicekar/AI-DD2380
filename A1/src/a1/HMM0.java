package a1;

import java.io.FileNotFoundException;
import static java.lang.Integer.parseInt;
import java.util.Scanner;

/**
 *
 * @author alicekarnsund
 */
public final class HMM0 {
    private Matrix A; 
    private Matrix B;
    private Matrix pi; 
    
    /** The constructor performs all necessary calculations and prints the answer
     * @param scannerIn */
    public HMM0(Scanner scannerIn) {
        try {
            this.readInput(scannerIn);
            this.calcNextObs();
        } catch (NullPointerException ne){
            //System.out.println("Some object does not exist! NullPointerException");
        }
    }
    
    /** A matrix class, with fields that describe both the dimension (rows, columns) and the values themselves (theMatrix) */
    public class Matrix {
        private int rows;
        private int columns;
        private double[][] theMatrix;
                
        public Matrix(String valueString){
            String[] valueArray = valueString.split(" ");
            rows = parseInt(valueArray[0]);
            columns = parseInt(valueArray[1]);
            theMatrix = new double[rows][columns];
        
            int index = 2;
            for (int i=0; i<rows; i++){
                for (int j=0; j<columns; j++){
                    theMatrix[i][j] = Double.parseDouble(valueArray[index]);
                    index++;
                } 
            }      
        }
        
        public Matrix(int rowsIn, int columnsIn){
            rows = rowsIn;
            columns = columnsIn;
            theMatrix = new double[rows][columns];
        }
        
        private void printMatrix(){
            String resultString = "";
            resultString = resultString + rows;
            resultString = resultString + " " + columns;
            for (int i=0; i<rows; i++){
                for (int j=0; j<columns; j++){
                    resultString = resultString + " " + theMatrix[i][j];
                }
            }
            System.out.println(resultString);
        }
        
        private void setElement(int row, int column, double element){
            theMatrix[row][column] = element;
        }
          
        private double getElement(int row, int column){
            return theMatrix[row][column];
        }
        
        private int getRows(){
            return(rows); 
        }
        
        private int getColumns(){
            return(columns); 
        }
    }
    
    /** A method that finds the input textfile and creates the matrices A, B, pi from it
     * @param scannerIn */
    public void readInput(Scanner scannerIn){
        A = new Matrix(scannerIn.nextLine());
        B = new Matrix(scannerIn.nextLine());
        pi = new Matrix(scannerIn.nextLine());
    }
    
    /** A method for multiplying two matrices
     * @param first
     * @param second
     * @return  */
    public Matrix matrixMult(Matrix first, Matrix second){
        if (first.getColumns() != second.getRows()){
            //System.out.println("Dimensions don't match! ");
            return null;
        }
        else{
            Matrix newMatrix = new Matrix(first.getRows(), second.getColumns());
            for(int i=0; i<first.getRows(); i++){
                for(int j=0; j<second.getColumns(); j++){
                    double newElement = 0;
                    for(int k=0; k<first.getColumns(); k++){
                        newElement = newElement + first.getElement(i,k)*second.getElement(k, j);
                    }
                    newMatrix.setElement(i, j, newElement);
                }
            }
            return(newMatrix);
        } 
    }
    
    /** For a state probability distribution in the previous step t-1, 
     * this method calculates the observation probability distribution at the current step t */
    public void calcNextObs(){
        Matrix result1 = matrixMult(pi, A);
        Matrix result2 = matrixMult(result1, B);
        
        String resultString = "";
        resultString = resultString + result2.getRows();
        resultString = resultString + " " + result2.getColumns();
        for (int i=0; i<result2.getRows(); i++){
            for (int j=0; j<result2.getColumns(); j++){ 
                resultString = resultString + " " + result2.getElement(i,j); 
            }
        }
        
        System.out.println(resultString);        
    }
    
    /** The main method reads the file name and constructs a HMM0 objec
     * @param args
     * @throws java.io.FileNotFoundException*/
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        HMM0 thisHMM0 = new HMM0(sc);
    }
}
