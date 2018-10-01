package a1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Integer.parseInt;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author alicekarnsund
 */
public class HMM0A {
    private Matrix A; 
    private Matrix B;
    private Matrix pi; 
    
    public HMM0A(String fileNameIn) {
        try {
            this.getMatrices(fileNameIn);
            this.calcNextObs();
        } catch (IOException | NullPointerException e) {
            System.out.println("Cannot open this file, try another!");
        }

    }
    public class Matrix {
        private final int rows;
        private final int columns;
        private final float[][] theMatrix;
        
        public Matrix(String valueString){
            String[] valueArray = valueString.split(" ");
            rows = parseInt(valueArray[0]);
            columns = parseInt(valueArray[1]);
            theMatrix = new float[rows][columns];
        
            int index = 2;
            for (int i=0; i<rows; i++){
                for (int j=0; j<columns; j++){
                    theMatrix[i][j] = Float.parseFloat(valueArray[index]);
                    index++;
                } 
            }     
            
        }
        
        public Matrix(int rowsIn, int columnsIn){
            rows = rowsIn;
            columns = columnsIn;
            theMatrix = new float[rows][columns];
        }
        
        private void setElement(int row, int column, float element){
            theMatrix[row][column] = element;
            
        }
          
        private float getElement(int row, int column){
            return theMatrix[row][column];
        }
        
        private int getRows(){
            return(rows); 
        }
        
        private int getColumns(){
            return(columns); 
        }
        private float[] getRow(int rowIn){
            float[] row = new float[columns];
            System.arraycopy(theMatrix[rowIn], 0, row, 0, columns);
            return(row);
        }
    }
    
    
    private Matrix matrixMult(Matrix first, Matrix second){
        if (first.getColumns() != second.getRows()){
            System.out.println("dimensions don't match! ");
            return null;
        }
        else{
            Matrix newMatrix = new Matrix(first.getRows(), second.getColumns());
            for(int i=0; i<first.getRows(); i++){
                for(int j=0; j<second.getColumns(); j++){
                    float newElement = 0;
                    for(int k=0; k<first.getColumns(); k++){
                        newElement = newElement + first.getElement(i,k)*second.getElement(k, j);
                    }
                    newMatrix.setElement(i, j, newElement);
                }
            }
            return(newMatrix);
        } 
    }
    
    private void calcNextObs(){
        Matrix result1 = matrixMult(pi, A);
        Matrix result2 = matrixMult(result1, B);
        
        String resultString = "";
        resultString = resultString + result2.getRows() + " ";
        resultString = resultString + result2.getColumns() + " ";
        for (int j=0; j<result2.getColumns(); j++){
           resultString = resultString + Float.toString(result2.getElement(0,j)) + " "; 
        }
        System.out.println(resultString);
    }
    
    private void getMatrices(String fileName) throws FileNotFoundException{
        File myFile = new File(fileName);
        Scanner fileScanner = new Scanner(myFile);
        String aLine = fileScanner.nextLine();
        String bLine = fileScanner.nextLine();    
        String piLine = fileScanner.nextLine();
        
        A = new Matrix(aLine);
        B = new Matrix(bLine);
        pi = new Matrix(piLine);
        //return(fileScanner.nextLine());
        //System.out.println(A.theMatrix[0][0]);
        //System.out.println(aLine);
    }
   

    
    public static void main(String[] args) throws FileNotFoundException {
        //Scanner sc = new Scanner(System.in);
        //String fileName = sc.nextLine();
        String fileName = "samplesHMM0/in";
        HMM0A thisA1 = new HMM0A(fileName);
        //thisA1.calcNextObs();
        //System.out.print(thisA1.getMatrices(fileName));
    }
    
}
