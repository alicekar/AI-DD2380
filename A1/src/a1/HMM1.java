
package a1;

/**
 *
 * @author alicekarnsund
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author alicekarnsund
 */
public class HMM1 {
    private Matrix A; 
    private Matrix B;
    private Matrix pi;
    private Sequence seq;              // timesteps
    
    private Matrix alpha;           // rows = states, columns = time steps
    
    
    /** The constructor performs all necessary calculations and prints the answer */
    public HMM1(Scanner scannerIn) {
        try {
            this.readInput(scannerIn);
            this.calcAlpha();
            System.out.println(this.getProb());
            //System.out.println(seq.getElement(0));
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
        
        private double[] getARow(int rowIndex){
            double[] result = new double[columns];
            for (int j=0; j<columns; j++){
                result[j] = theMatrix[rowIndex][j];
            }
            return result;
        } 
        
        private double[] getAColumn(int colIndex){
            double[] result = new double[rows];
            for (int i=0; i<rows; i++){
                result[i] = theMatrix[i][colIndex];
            }
            return result;
        } 
        
        private int getRows(){
            return(rows); 
        }
        
        private int getColumns(){
            return(columns); 
        } 
    }
    
    /** A matrix class, with fields that describe both the number of time steps (length) and the values themselves (theSeq) */
    public class Sequence{
        int length;
        int[] theSeq;
        
        public Sequence(String seqIn){
            String[] seqArray = seqIn.split(" ");
            length = parseInt(seqArray[0]);
            theSeq = new int[length];
            
            int index = 1;
            for (int i=0; i<length; i++){
                theSeq[i] = parseInt(seqArray[index]);
                index++;
            }
        }
        
        private void printSequence(){
            String resultString = "";
            resultString = resultString + length;
            for (int i=0; i<length; i++){
               resultString = resultString + " " + theSeq[i];
            }
            System.out.println(resultString);
        }
        
        private int getLength(){
            return length;
        }
        
        private int getElement(int index){
            return theSeq[index];
        }
    }
        
    /** A method that finds the input textfile and creates the matrices A, B, pi from it */
    public void readInput(Scanner scannerIn){
        A = new Matrix(scannerIn.nextLine());
        B = new Matrix(scannerIn.nextLine());
        pi = new Matrix(scannerIn.nextLine());
        seq = new Sequence(scannerIn.nextLine());
    }
    
    /** A method for multiplying two matrices */
    public Matrix matrixMult(Matrix first, Matrix second){
        if (first.getColumns() != second.getRows()){
            //System.out.println("dimensions don't match! ");
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
    
    /* A method that calculates all alpha values for A, B, pi and seq and stores them in a matrix*/
    public void calcAlpha(){
        alpha = new Matrix(A.getRows(), seq.getLength());
        
        // initial step
        double[] b1 = B.getAColumn(seq.getElement(0));
        for (int i=0; i<b1.length; i++){
            double alphai = b1[i]*pi.getElement(0, i);
            alpha.setElement(i, 0, alphai);
        }
        
        // t up to , start at 1 since we already have made the initial step
        for (int t=1; t<seq.length; t++){
            double[] bt = B.getAColumn(seq.getElement(t));
            
            for (int i=0; i<bt.length; i++){        // state i at time t
                double sum = 0;
                
                for (int j=0; j<bt.length; j++){    // state j at time t-1
                    sum = sum + A.getElement(j, i)* alpha.getElement(j,t-1);
                }
                
                double alphai = sum*bt[i];
                alpha.setElement(i, t, alphai);
            }
        }
    }
    
    /** A method that calculates the probability of an observation sequence o_1:T */
    public double getProb(){
        double[] lastAlpha = alpha.getAColumn(seq.length-1);
        double prob = 0;
        for (int i=0; i<lastAlpha.length; i++){   // marginalize over all possible states to be in at T
            prob = prob + lastAlpha[i];
        }
        return prob;
    }
     
    /** The main method reads the file name and constructs a HMM1 object*/
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        HMM1 thisHMM1 = new HMM1(sc);
    }
    
}