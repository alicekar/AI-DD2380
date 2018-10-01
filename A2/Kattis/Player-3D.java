import java.util.*;
import java.lang.Math;

public class Player {
    
    private int whoIAm;
    private int opponent;
    private int BOARD_SIZE;
    private int MAX_DEPTH = 1;
    
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
        
        // All possible next states
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);                                
        
        // Make the move that gives the largest evaluation value in alpha beta pruning
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
                int[] bestPossible = {Integer.MIN_VALUE,0};
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
                int[] bestPossible = {Integer.MAX_VALUE,0};
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
        int[] me = new int[76];
        int[] opp = new int[76];
        
        for (int i=0; i<76; i++){
            me[i] = 0;
            opp[i] = 0;
        }
        
        // Go through all lines
        for (int i=0; i<BOARD_SIZE; i++){
            for (int j=0; j<BOARD_SIZE; j++){
                for (int k=0; k<BOARD_SIZE; k++){
                    // Rows in each direction
                    if (gameState.at(i,j,k)==whoIAm){
                        me[4*i+j]++;
                    }
                    else if (gameState.at(i,j,k)==opponent){
                        opp[4*i+j]++;
                    }
                    if (gameState.at(i,k,j)==whoIAm){
                        me[4*i+j+16]++;
                    }
                    else if (gameState.at(i,k,j)==opponent){
                        opp[4*i+j+16]++;
                    }
                    if (gameState.at(k,i,j)==whoIAm){
                        me[4*i+j+32]++;
                    }
                    else if (gameState.at(k,i,j)==opponent){
                        opp[4*i+j+32]++;
                    }
                }
                // 2D diagonals
                if (gameState.at(i,j,j)==whoIAm){
                    me[i+48]++;
                }
                else if (gameState.at(i,j,j)==opponent){
                    opp[i+48]++;
                }
                if (gameState.at(i,j,(BOARD_SIZE-j-1))==whoIAm){
                    me[i+52]++;
                }
                else if (gameState.at(i,j,(BOARD_SIZE-j-1))==opponent){
                    opp[i+52]++;
                }
                if (gameState.at(j,i,j)==whoIAm){
                    me[i+56]++;
                }
                else if (gameState.at(j,i,j)==opponent){
                    opp[i+56]++;
                }
                if (gameState.at(j,i,(BOARD_SIZE-j-1))==whoIAm){
                    me[i+60]++;
                }
                else if (gameState.at(j,i,(BOARD_SIZE-j-1))==opponent){
                    opp[i+60]++;
                }
                if (gameState.at(j,j,i)==whoIAm){
                    me[i+64]++;
                }
                else if (gameState.at(j,j,i)==opponent){
                    opp[i+64]++;
                }
                if (gameState.at(j,(BOARD_SIZE-j-1),i)==whoIAm){
                    me[i+68]++;
                }
                else if (gameState.at(j,(BOARD_SIZE-j-1),i)==opponent){
                    opp[i+68]++;
                }
            }
            // 3D diagonals
            if (gameState.at(i,i,i)==whoIAm){
                me[72]++;
            }
            else if (gameState.at(i,i,i)==opponent){
                opp[72]++;
            }
            if (gameState.at(i,(BOARD_SIZE-i-1),i)==whoIAm){
                me[73]++;
            }
            else if (gameState.at(i,(BOARD_SIZE-i-1),i)==opponent){
                opp[73]++;
            }
            if (gameState.at((BOARD_SIZE-i-1),(BOARD_SIZE-i-1),i)==whoIAm){
                me[74]++;
            }
            else if (gameState.at((BOARD_SIZE-i-1),(BOARD_SIZE-i-1),i)==opponent){
                opp[74]++;
            }
            if (gameState.at((BOARD_SIZE-i-1),i,i)==whoIAm){
                me[75]++;
            }
            else if (gameState.at((BOARD_SIZE-i-1),i,i)==opponent){
                opp[75]++;
            }
        }
        
        for (int i=0; i<76; i++){
            sum += valueLine(me[i], opp[i]);
        }
        
        return sum;
    }
    
//______________________________________________________________________________

    // Calculates the calue for each row based on the amount of X:es and O:s    
    public int valueLine(int meIn, int oppIn){
        int result = 0;
        
        // If a line is free, then it is better to have more symbols in that line 
        if (meIn == 0 && oppIn>1){
            result = (int)-Math.pow(10, oppIn);
        }
        else if (meIn>1 && oppIn==0){
            result = (int)Math.pow(10, meIn);
            
        // If a line is blocked, then it is better to have fewer symbols in that line 
        }else{
            int diff = meIn-oppIn;
            if (diff >0){
                result = (int)-Math.pow(10, diff);
            }else if (diff <0){
                result = (int)Math.pow(10, -diff);
            }
        }
        
        return result;
       
    }
}
