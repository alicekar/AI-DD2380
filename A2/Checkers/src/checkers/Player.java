import java.util.*;

public class Player {
    // Constant in the whole game
    private int BOARD_SIZE; // =32        
    private int MAX_MOVES;  // =50
    
    // Constants in each GameState
    private int whoIAm;
    private int opponent;
    
    // Weights for heuristic function, a checker in the middle is more threatened       see http://tim.hibal.org/blog/playing-checkers-with-minimax-continued/
    private int[] weights = { 4, 4, 4, 4, 
                             4, 3, 3, 3, 
                              3, 2, 2, 4, 
                             4, 2, 1, 3, 
                              3, 1, 2, 4, 
                             4, 2, 2, 3, 
                              3, 3, 3, 4, 
                             4, 4, 4, 4};

//______________________________________________________________________________    
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {
        
        // Set constants
        BOARD_SIZE = pState.NUMBER_OF_SQUARES;                                  
        MAX_MOVES = pState.MOVES_UNTIL_DRAW;                                    
        whoIAm = pState.getNextPlayer();
        if (whoIAm == Constants.CELL_RED){
            opponent = Constants.CELL_WHITE;
        }else{
            opponent = Constants.CELL_RED;
        }
                
    
        // Find all possible next states
        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);                                   
                
        // Iterative deepening until a winning state is found OR runs out of time       
        int stateIndex = 0;
        int evaluation = Integer.MIN_VALUE;
        int max_depth = 0;
        boolean keepGoing = true;
        
        while (pDue.timeUntil() > 700000000 && keepGoing){
            
            System.err.println("Depth " + max_depth);
            int[] result = alphabeta(pState, max_depth, Integer.MIN_VALUE, Integer.MAX_VALUE, whoIAm);
            
            evaluation = result[0];
            stateIndex = result[1];
            
            if (result[0] == 100000){                                          
                System.err.println("Win found with alpha beta! ");
                keepGoing = false;
            }
            
            if (result[0] == -100000){                                          
                System.err.println("Loss found with alpha beta! ");
                keepGoing = false;
            }
            
            
            System.err.println("Best result " + evaluation + " with state index " + stateIndex);
            max_depth++;
        }
        
        return nextStates.elementAt(stateIndex);
    }
    
//______________________________________________________________________________    
    
    public int[] alphabeta(GameState gameState, int depth, int alpha, int beta, int player){                                  
                
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);                                
        
        // If no possible moves, the game is over
        if (nextStates.isEmpty()){
            if (player == whoIAm){
                int[] result = {1000000,0};
                return result;
            }else{
                int[] result = {-1000000,0};
                return result;
            }
        }else{
            // If we have reached the maximum depth, call the evaluation function
            if (depth == 0) {
                int v = evalFunc(gameState);
                int[] result = {v,0};
                return result;
            }else{
                
                // If I am playing, I will choose the MAXIMUM value
                if (player == whoIAm){                                 
                    int[] bestPossible = {-100000,0};
                    for (int i=0; i<nextStates.size(); i++){
                        
                        int[] node = {alphabeta(nextStates.get(i), depth-1, alpha, beta, nextStates.get(i).getNextPlayer())[0], i};
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
                        int[] node = {alphabeta(nextStates.get(i), depth-1, alpha, beta, nextStates.get(i).getNextPlayer())[0], i};
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
    }

    
//______________________________________________________________________________    
    
    // The evaluation function, estimates how good a state is
    public int evalFunc(GameState stateIn){
        int white = 0;
        int red = 0;
        
        for (int i=0; i<BOARD_SIZE; i++){
            if (stateIn.get(i) != Constants.CELL_EMPTY){
                if (stateIn.get(i) == 1){
                    red += weights[i];              // weight = 1,2,3 or 4
                    //red += 1000;
                }
                if (stateIn.get(i) == 5){
                    red += weights[i] + 4;          // 4 extra points for king
                    //red += 1500;
                }
                if (stateIn.get(i) == 2){
                    white += weights[i];
                    //white += 1000;
                }
                if (stateIn.get(i) == 6){
                    white += weights[i] + 4;
                    //white += 1500;
                }
            }
        }
   
        // If I am red, return red-white
        if (whoIAm == 1){                   
            int result = red-white;
            return result;
        // If I am white, return white-red
        }else{
            int result = white-red;
            return result;
        }
    }    
}

