import java.util.*;

public class Player {
    // 2D
    private int whoIAm;
    private int opponent;
    private final int BOARD_SIZE = GameState.BOARD_SIZE;
    private final int MAX_DEPTH = 3;
    
    
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
        whoIAm = gameState.getNextPlayer();
        if (whoIAm == Constants.CELL_X){
            opponent = Constants.CELL_O;
        }else{
            opponent = Constants.CELL_X;
        }
        
        
        // Find all possible states
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);    
        
        if (nextStates.size() == 0) {
            return new GameState(gameState, new Move());
        }
        
        // Make the move that gives the largest evaluation value in alphabeta
        int stateIndex = alphabeta(gameState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE)[1];
        return nextStates.elementAt(stateIndex);
    }    
    
    public int[] alphabeta(GameState gameState, int depth, int alpha, int beta){                        
        // nextStates now contain all possible next states
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);    
        
        // If we have reached the maximum depth, call the evaluation function
        if (depth == 0 || nextStates.isEmpty()) {
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
                        break;          // beta prune
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
                        break;          // alpha prune
                    }
                }
                return bestPossible;
            }
        }        
        
    }
        /**
     * 
     * @param gameState
     *          current state being analyzed
     * @return a score which tells how useful a certain state is for the player
     * notice that there are 3 different ways in winning the game; 1. 4 marks 
     * along a row, 2. 4 marks alnong a column, 3. 4 marks across any of the
     * two diagonals.
     * But the rewards of X should of course also depend on the moves of O, which
     * valueLine takes care of
     */
    // The evaluation function, estimates how good a state is
    public int evaluation(GameState gameState){
        int sum = 0;
        int[] me = new int[10];  // 10 element, 0->9
        int[] opp = new int[10];  
        
        
        // Evaluate case 1: marks along a row
        for (int row=0; row<BOARD_SIZE; row++){
            for (int col=0; col<BOARD_SIZE; col++){
                if (gameState.at(row,col)==whoIAm){
                    me[row]++;
                }
                if (gameState.at(row,col)==opponent){
                    opp[row]++;
                }
            }
        }
        
        //Evaluate case 2: marks along a column
        for(int col=0; col<BOARD_SIZE; col++){
            for(int row=0; row<BOARD_SIZE; row++){
                if(gameState.at(row, col)==whoIAm){
                    me[4+col]++;
                }
                if(gameState.at(row, col)==opponent){
                    opp[4+col]++;
                }
            }
        }
        
        //Evaluate case 3: marks along any of the two diagonals
        for (int d=0; d<BOARD_SIZE; d++){
            if (gameState.at(d,d)==whoIAm){
                me[8]++;
            }
            if (gameState.at(d,d)==opponent){
                opp[8]++;
            }
        }
        
        for (int d=0; d<BOARD_SIZE; d++){
            if (gameState.at(d,(BOARD_SIZE-d-1))==whoIAm){
                me[9]++;
            }
            if (gameState.at(d,(BOARD_SIZE-d-1))==opponent){
                opp[9]++;
            }
        }
        /**
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
        }*/
        for(int i = 0; i<10; i++){
            sum += valueLine(me[i], opp[i]);
        }
        
        return sum;
    }
    
    // Calculates the value for each line based on the amount of X's and O's
    public int valueLine(int meIn, int oppIn){
        int result = 0;
        
        // If a line is free then it is better to have more symbols in that line
        if (meIn==0 && oppIn>0){
            result = (int)-Math.pow(10,oppIn);
        }else if(meIn>0 && oppIn==0){
            result = (int)Math.pow(10,meIn);
        }  
        return result;
    } 
    
}
