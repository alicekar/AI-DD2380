/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import static java.lang.Integer.parseInt;
import java.util.Scanner;

/**
 *
 * @author elin
 */
public class HMM2 {
    private Matrix A; 
    private Matrix B;
    private Matrix pi; 
    private Sequence seq;              // timesteps
    
    private Matrix delta;           // rows = states, columns = time steps
    private Matrix deltaIndex;      // rows = states, columns = time steps
    
    public HMM2(Scanner scannerIn) {
        try {
            this.readInput(scannerIn);
            this.calcDelta();
            Sequence stateSeq = this.findStateSeq();
            stateSeq.printOnlySequence();
        } catch (NullPointerException ne){
            System.out.println("Cannot open this file, try another! NullPointerException");
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
        
        public Sequence(int len){
            length = len;
            theSeq = new int[length];
        }
        
        private void printSequence(){
            String resultString = "";
            resultString = resultString + length;
            for (int i=0; i<length; i++){
               resultString = resultString + " " + theSeq[i];
            }
            System.out.println(resultString);
        }
        
        private void printOnlySequence(){
            String resultString = "";
            resultString = resultString + theSeq[0];
            for (int i=1; i<length; i++){
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
        
        private void setElement(int index, int e){
            theSeq[index] = e;
        }
    }
        
    /** A method that finds the input textfile and creates the matrices A, B, pi from it */
    public void readInput(Scanner scannerIn){
        A = new Matrix(scannerIn.nextLine());
        B = new Matrix(scannerIn.nextLine());
        pi = new Matrix(scannerIn.nextLine());
        seq = new Sequence(scannerIn.nextLine());
    }
    
    /* A method that calculates all delta values for A, B, pi and seq and stores them in a matrix*/
    private void calcDelta(){
        delta = new Matrix(A.getRows(), seq.length);
        deltaIndex = new Matrix(A.getRows(), seq.length);
        
        // initial step
        double[] b1 = B.getAColumn(seq.getElement(0));
        for (int i=0; i<b1.length; i++){
            double deltai = b1[i]*pi.getElement(0, i);
            delta.setElement(i, 0, deltai);
        }
        
        // t up to , start at 1 since we already have made the initial step
        for (int t=1; t<seq.length; t++){
            double[] bt = B.getAColumn(seq.getElement(t));
            
            for (int i=0; i<bt.length; i++){        // state i at time t
                double deltai = 0;
                int deltaiIndex = 0;
                
                for (int j=0; j<bt.length; j++){    // state j at time t-1
                    double deltaij = A.getElement(j, i)* delta.getElement(j,t-1) * bt[i];
                    
                    if (deltaij > deltai){
                        deltai = deltaij;
                        deltaiIndex = j;
                    }
                }
                
                delta.setElement(i, t, deltai);
                deltaIndex.setElement(i, t, deltaiIndex);
            }
        }
    }
    
    /** Given an observation sequence o_1:T, 
     * this method calculates the most likely state sequence x_1:T */
    private Sequence findStateSeq(){
        double[] lastDelta = delta.getAColumn(seq.getLength()-1);
        double maxDelta = 0;
        int maxDeltaIndex = 0; 
        Sequence result = new Sequence(seq.getLength());
        
        for (int i=0; i<lastDelta.length; i++){
            if (lastDelta[i] > maxDelta){
                maxDelta = lastDelta[i];
                maxDeltaIndex = i;
            }
        }
        System.out.println(maxDeltaIndex);
        result.setElement(seq.getLength()-1, maxDeltaIndex);
        
        int currentIndex = maxDeltaIndex;
        for (int t=seq.length-1; t>0; t--){
            int bestPrevState = (int)deltaIndex.getElement(currentIndex, t); // stores the best previous step
            result.setElement(t-1, bestPrevState);
            currentIndex = bestPrevState;
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HMM2 thisHMM2 = new HMM2(sc);
    }
}
