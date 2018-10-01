import java.util.*;
import java.lang.Math;

public class Player {
    
    private int whoIAm;
    private int opponent;
    private int BOARD_SIZE;
    private int MAX_DEPTH = 3;
    
    
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        
        // Set constants
        whoIAm = gameState.getNextPlayer();                                     // player: X = 1, O = 2 
        if (whoIAm == Constants.CELL_X){
            opponent = Constants.CELL_O;
        }else{
            opponent = Constants.CELL_X;
        }
        BOARD_SIZE = gameState.BOARD_SIZE;
        
        // Find all possible next states
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);                                
        
        // Make the move that gives the largest evaluation value in alphabeta
        int stateIndex = alphabeta(gameState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE)[1];
        return nextStates.elementAt(stateIndex);
    }    
    
//______________________________________________________________________________    
    
    public int[] alphabeta(GameState gameState, int depth, int alpha, int beta){                           
        
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);                                
        
        // If we have reached the maximum depth, call the evaluation function
        if (depth == 0) {
            int v = evaluation(gameState);
            int[] best = {v,0};
            return best;
            
        }else{  
            
            // If I am playing, I will choose the MAXIMUM value
            if (gameState.getNextPlayer() == whoIAm){                                 
                int[] bestPossible = {-100000,0};
                for (int i=0; i<nextStates.size(); i++){
                    int[] node = {alphabeta(nextStates.get(i), depth-1, alpha, beta)[0], i};
                    if (node[0] > bestPossible[0]){
                        bestPossible = node;
                        if (bestPossible[0]>alpha){
                            alpha = bestPossible[0];
                        }
                    }
                    if (beta<=alpha){
                        break;
                    }
                }
                return bestPossible;
                        
            // If the opponent is playing, he/she will choose the MINIMUM value                
            }else{                                                              
                int[] bestPossible = {100000,0};
                for (int i=0; i<nextStates.size(); i++){
                    int[] node = {alphabeta(nextStates.get(i), depth-1, alpha, beta)[0], i};
                    if (node[0] < bestPossible[0]){
                        bestPossible = node;
                        if (bestPossible[0]<beta){
                            beta = bestPossible[0];
                        }
                    }
                    if (beta<=alpha){
                        break;
                    }
                }
                return bestPossible;
            }
        }        
        
    }
    
//______________________________________________________________________________    
    
    // The evaluation function, estimates how good a state is
    public int evaluation(GameState gameState){
        int sum = 0;
        int[] me = {0,0,0,0,0,0,0,0,0,0};
        int[] opp = {0,0,0,0,0,0,0,0,0,0};  
        
        // Go through all lines
        for (int i=0; i<BOARD_SIZE; i++){
            for (int j=0; j<BOARD_SIZE; j++){
                
                // 4 rows
                if (gameState.at(i,j)==whoIAm){
                    me[i]++;
                }
                else if (gameState.at(i,j)==opponent){
                    opp[i]++;
                }
                
                // 4 columns
                if (gameState.at(j,i)==whoIAm){
                    me[i+4]++;
                }
                else if (gameState.at(j,i)==opponent){
                    opp[i+4]++;
                }
            }
            
            // 2 diagonals
            if (gameState.at(i,i)==whoIAm){
                me[8]++;
            }
            else if (gameState.at(i,i)==opponent){
                opp[8]++;
            }
            if (gameState.at(i,(BOARD_SIZE-i-1))==whoIAm){
                me[9]++;
            }
            else if (gameState.at(i,(BOARD_SIZE-i-1))==opponent){
                opp[9]++;
            }
        }
        
        for (int i=0; i<10; i++){
            sum += valueLine(me[i], opp[i]);
        }
        
        return sum;
    }

//______________________________________________________________________________
    
    // Calculates the calue for each row based on the amount of X:es and O:s
    public int valueLine(int meIn, int oppIn){
        int result = 0;
        
        // If a line is free, then it is better to have more symbols in that line 
        if (meIn == 0 && oppIn>0){
            result = (int)-Math.pow(10, oppIn);        
        }
        else if (meIn>0 && oppIn==0){
            result = (int)Math.pow(10, meIn);        
        }
        
        return result;
            
    }
}
