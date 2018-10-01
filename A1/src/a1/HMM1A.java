
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
public class HMM1A {
    private Matrix A; 
    private Matrix B;
    private Matrix pi; 
    //private Matrix O;
    private float[] O;
    private float[] alphaArray1;
    //private float[] alphaArray;
    private Matrix alphaMatrix;
    private float[] innerAlpha;
    
    public HMM1A(String fileNameIn) {
        try {
            this.getMatrices(fileNameIn);
            //this.getFirstAlpha();
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
        
        private float[] getColumn(int columnIn){
            float[] column = new float[rows];
            for(int i=0; i<rows; i++){
               column[i] = theMatrix[i][columnIn]; 
            }
            return(column);
        }    
    }
        
    
    private float[] getObs(String obsIn){
        String[] obsArray = obsIn.split(" ");
        int columns = parseInt(obsArray[0]);
        float[] obsMatrix = new float[columns];
            int index = 1;
            for (int j=0; j<columns; j++){
                    obsMatrix[j] = Float.parseFloat(obsArray[index]);
                    index++;
                } 
        return(obsMatrix);
        
    }
    
    private void getMatrices(String fileName) throws FileNotFoundException{
        File myFile = new File(fileName);
        Scanner fileScanner = new Scanner(myFile);
        String aLine = fileScanner.nextLine();
        String bLine = fileScanner.nextLine();    
        String piLine = fileScanner.nextLine();
        String oLine = fileScanner.nextLine();
        
        A = new Matrix(aLine);
        B = new Matrix(bLine);
        pi = new Matrix(piLine);
        O = this.getObs(oLine);
        //System.out.println(B.getRows());
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
           resultString = resultString + result2.getElement(0,j) + " "; 
        }
        System.out.println(resultString);
        
       
    }

    private Matrix calcFirstAlpha(Matrix bIn, Matrix piIn, float[] obsIn){
        alphaArray1 = new float[bIn.getRows()];
        alphaMatrix = new Matrix(obsIn.length,bIn.getRows());
        int obs = (int)obsIn[4];
        if (piIn.getColumns() != bIn.getRows()){
            System.out.println("dimensions don't match! ");    
        }
        else
            for(int j=0; j<alphaArray1.length; j++){
                float theElement = bIn.getElement(j, obs)*piIn.getElement(0, obs);
                alphaMatrix.setElement(0, j, theElement); 
                //alphaArray1[j] = M1.getElement(j, obs)*M2.getElement(0, obs);
            }
        return(alphaMatrix);
    }
    
    private void getFirstAlpha(){
        calcFirstAlpha(B,pi,O);
        calcAlphas(B,A,O);
        float answer = calcProbObsSeq(alphaMatrix.getRow(alphaMatrix.getRows()-1));
        //System.out.println(alphaMatrix.getElement(0, 0));
       // System.out.println(Arrays.toString(calcFirstAlpha(B,pi,O).getRow(0)));
        for(int i=0; i<alphaMatrix.getRows(); i++){
            System.out.println(Arrays.toString(alphaMatrix.getRow(i)));
        }
        //System.out.println(Arrays.toString(alphaMatrix.getColumn(0)));
        System.out.println(answer);
    }
    
    private Matrix calcAlphas(Matrix bIn, Matrix aIn, float[] obsIn){
        for(int t=1; t<obsIn.length; t++){
            float alphaElement;
            for(int i=0; i<bIn.getRows(); i++){
                float sumElement = 0;
                for(int j=0; j<aIn.getRows(); j++){
                    sumElement = sumElement + aIn.getElement(j, i)*alphaMatrix.getRow(t-1)[j];
                }
                
                alphaElement = bIn.getElement(i, (int) obsIn[t])*sumElement;
                alphaMatrix.setElement(t, i, alphaElement); 
            }
            
        }
        System.out.println(alphaMatrix.getRows());
        return(alphaMatrix);
    }
   
    private float calcProbObsSeq(float[] probSeqIn){
        float prob = 0;
        for(int i=0; i<probSeqIn.length; i++){
            prob = prob + probSeqIn[i];
        }
        return(prob);
    }
    
    public static void main(String[] args){
        //Scanner sc = new Scanner(System.in);
        //String fileName = sc.nextLine();
        String fileName = "samplesHMM1/in";
        HMM1A thisA1 = new HMM1A(fileName);
        //thisA1.getOtherAlphas();
        thisA1.getFirstAlpha();
        //System.out.println(thisA1.O[1]);
        //System.out.println(thisA1.firstAlpha(pi, B));
        //System.out.print(thisA1.getMatrices(fileName));
    }
    
}
