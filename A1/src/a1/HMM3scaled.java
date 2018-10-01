
package a1;

/**
 *
 * @author alicekarnsund
 */

import static java.lang.Integer.parseInt;
import java.util.Scanner;

/**
 *
 * @author elinsamuelsson
 */
public class HMM3scaled {
    private int states;
    private int obs;
    private int timeSteps;
    
    private Matrix A; 
    private Matrix B;
    private Matrix pi;
    private Sequence seq;              // timesteps
    
    private Matrix alpha;           // rows = states, columns = time steps
    private double[] c;
    private Matrix beta;            // rows = states, columns = time steps
    private Matrix[] digamma;       // for each time step t, there is a matrix with rows = states, columns = states 
    private Matrix[] gamma;         //      
    
    private int lengtLimit = 5000;
    private int maxIters = 1000000;//Integer.MAX_VALUE; //55
    private int iters = 0;
    private double oldLogProb = -1000000000;
    
    public HMM3scaled(Scanner scannerIn) {
        try {
            this.readInput(scannerIn);         
            this.train();
        } catch (Exception e) {
            //System.out.println(e);
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
    
    /** A sequence class, with fields that describe both the number of time steps (length) and the values themselves (theSeq) */
    public class Sequence{
        int length;
        int[] theSeq;
        
        public Sequence(String seqIn){
            String[] seqArray = seqIn.split(" ");
            //length = parseInt(seqArray[0]);
            length = lengtLimit;
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
        
    /** A method that finds the input textfile and creates the matrices A, B, pi and seq from it */
    public void readInput(Scanner scannerIn){
        A = new Matrix(scannerIn.nextLine());
        B = new Matrix(scannerIn.nextLine());
        pi = new Matrix(scannerIn.nextLine());
        seq = new Sequence(scannerIn.nextLine());
        
        states = A.getRows();
        obs = B.getColumns();
        timeSteps = seq.getLength();
    }
    
    /** A method for multiplying two matrices */
    private Matrix matrixMult(Matrix first, Matrix second){
        if (first.getColumns() != second.getRows()){
            System.out.println("dimensions don't match! ");
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
    
    /* A method that calculates all alpha values for A, B, pi and seq and stores them in a matrix
     * alpha_i,t = the probability to be in state i at time t AND having observed the seq o_1:t */
    private void calcAlpha(){
        alpha = new Matrix(states, timeSteps);
        c = new double[timeSteps];    //scalefactors
        
        // initial step - NORMALIZED
        double c0 = 0;
        double[] b0 = B.getAColumn(seq.getElement(0));
        for (int i=0; i<states; i++){
            double alphai = b0[i]*pi.getElement(0, i);
            c0 = c0 + alphai;
            alpha.setElement(i, 0, alphai);
        }
        //scale alpa0
        c0 = 1/c0;
        c[0]=c0;
        for (int i=0; i<states; i++){
            double normAlphai = alpha.getElement(i, 0)*c0;
            alpha.setElement(i, 0, normAlphai);
        }
        
        // t up to T-1, start at 1 since we already have made the initial step
        for (int t=1; t<timeSteps; t++){
            double ct = 0;
            double[] bt = B.getAColumn(seq.getElement(t));
            
            for (int i=0; i<states; i++){        // state i at time t
                double sum = 0;                     // aka alpha.t
                for (int j=0; j<states; j++){    // state j at time t-1
                    sum = sum + A.getElement(j, i)* alpha.getElement(j,t-1);
                }
                double alphai = sum*bt[i];
                ct = ct + alphai;
                alpha.setElement(i, t, alphai);
            }
            
            ct = 1/ct;
            c[t]=ct;
            for (int i=0; i<states; i++){
                double normAlphai = alpha.getElement(i, t)*ct;
                alpha.setElement(i, t, normAlphai);
            }
        }
    }
    
    /* A method that calculates all beta values for A, B, pi and seq and stores them in a matrix
     * beta_i,t = the probability of observing the future seq o_t+1:T GIVEN that we are in state i at time t */
    private void calcBeta(){
        beta = new Matrix(states, timeSteps);
        
        // initial step
        for (int i=0; i<states; i++){
            beta.setElement(i, timeSteps-1, c[timeSteps-1]);
        }
        
        // t down to 0, start at T-2 since we already have made the initial step T-1
        for (int t=timeSteps-2; t>=0; t--){
            double[] bt = B.getAColumn(seq.getElement(t+1));
            
            for (int i=0; i<states; i++){        // state i at time t
                double sum = 0;
                for (int j=0; j<states; j++){    // state j at time t-1
                    sum = sum + A.getElement(i, j)* beta.getElement(j,t+1)*bt[j];
                }
                double normBetai = sum*c[t];
                beta.setElement(i, t, normBetai);
            }
        }
    }

    /* A method that calculates all gamma values for A, B, pi, seq, alpha and beta and stores them in a matrix
     * digamma_i,j,t = the probability of being in state i at time t AND state j at time t+1 GIVEN the observation seq o_1:T
     * gamma_i,t = the probability of being in state i at time t GIVEN the observation seq o_1:T */
    private void calcGamma(){
        digamma = new Matrix[timeSteps];
        gamma = new Matrix[timeSteps];
        
        // t from 0 up to T-2
        for (int t=0; t<timeSteps-1; t++){
            double denom = 0;
            double[] bt = B.getAColumn(seq.getElement(t+1));
            
            for (int i=0; i<states; i++){
                for (int j=0; j<states; j++){
                    denom = denom + alpha.getElement(i, t) * A.getElement(i,j) * bt[j] * beta.getElement(j, t+1);
                }
            }
            
            Matrix digammaMatrix = new Matrix(states, states);
            Matrix gammaMatrix = new Matrix(states, 1);
            
            for (int i=0; i<states; i++){  
                double gammai = 0;
                for (int j=0; j<states; j++){
                    double digammaij = alpha.getElement(i, t) * A.getElement(i,j) * bt[j] * beta.getElement(j, t+1)/denom;
                    digammaMatrix.setElement(i, j, digammaij);
                    gammai = gammai + digammaij;
                }
                gammaMatrix.setElement(i, 0, gammai);
            }
                
            digamma[t] = digammaMatrix;
            gamma[t] = gammaMatrix;
        }
        
        // last time step T-1, only use alphas
        double denom = 0;
        for (int i=0; i<states; i++){
            denom = denom + alpha.getElement(i,timeSteps-1);
        }
        Matrix gammaMatrix = new Matrix(states, 1);
        for (int i=0; i<states; i++){
            double gammai = alpha.getElement(i,timeSteps-1)/denom;
            gammaMatrix.setElement(i, 0, gammai);
        }
        gamma[timeSteps-1] = gammaMatrix;
    }
    
    /**A method that updates A,B and pi */
    private void updateMatrices(){
        
        //Update A
        for (int i=0; i<states; i++){
            for (int j=0; j<A.getColumns(); j++){
                double numer = 0;
                double denom = 0;
                
                for (int t=0; t<timeSteps-1;t++){
                    numer = numer + digamma[t].getElement(i, j);
                    denom = denom + gamma[t].getElement(i, 0);
                }
                
                double theElement = numer/denom;
                A.setElement(i, j, theElement);
            }
        }
        
        // Update B
        for (int i=0; i<states; i++){
            for (int j=0; j<obs; j++){
                double numer = 0;
                double denom = 0;
                
                for (int t=0; t<timeSteps; t++){
                    if (seq.getElement(t) == j){
                        numer = numer + gamma[t].getElement(i, 0);
                    }
                    denom = denom + gamma[t].getElement(i, 0);
                }
                double theElement = numer/denom;
                B.setElement(i, j, theElement);
            }
        }
              
        // Update pi
        for (int i=0; i<pi.getColumns(); i++){
            pi.setElement(0, i, gamma[0].getElement(i, 0));
        }
    }
    
    /** A method that is used to verify that P(O|lamda) is increasing */
    public double calcProb(){
        double logProb = 0;
        for (int t=0; t<timeSteps; t++){
            logProb = logProb + Math.log(c[t]);
        }
        logProb = -logProb;
        return logProb;
    }
    
    /** A method that iteratively trains = updates the model (A,B,pi) */
    public void train(){
        Boolean keepGoing = true;
        
        while (keepGoing){
            this.calcAlpha();
            this.calcBeta();
            this.calcGamma();
            this.updateMatrices();
            
            double logProb = this.calcProb();
            iters++;
            if (iters < maxIters && logProb > oldLogProb){
                oldLogProb = logProb;
            }else{
                A.printMatrix();
                B.printMatrix();
                pi.printMatrix();
                keepGoing = false;
                System.out.println(iters);
            }
        }
    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        HMM3scaled thisHMM3scaled = new HMM3scaled(sc);
    }
    
}