import java.util.*;

public class PlayerA {
    //3D
    private int whoIAm;
    private int opponent;
    private int boardSize = GameState.BOARD_SIZE;
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
        whoIAm = gameState.getNextPlayer();
        if (whoIAm == Constants.CELL_X){
            opponent = Constants.CELL_O;
        }else{
            opponent = Constants.CELL_X;
        }
        
        
        
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);                                // nextStates now contain all possible next states
        
        //if (nextStates.size() == 0) {
        //    return new GameState(gameState, new Move());
        //}
        
        int stateIndex = alphabeta(gameState, Integer.MIN_VALUE, MAX_DEPTH, Integer.MAX_VALUE)[1];
        return nextStates.elementAt(stateIndex);
    }    
    
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
     * notice that there are 3 different ways in winning the game for each layer;   
     * 1) 4 marks along any of the 48 orthogonal lines, 2) 4 marks along any of 
     * the 24 diagonal lines, 3) 4 marks across any of the 4 main diagonals.
     * But the rewards of X should of course also depend on the moves of O, which
     * valueLine takes care of
     */
    public int evaluation(GameState gameState){
        int sum = 0;
        int[] me = new int[76];
        int[] opp = new int[76];  
        
        // Evaluate case 1: marks along each row for each layer (16)
        for (int layer=0; layer<boardSize; layer++){
            for (int row=0; row<boardSize; row++){
                for (int col=0; col<boardSize; col++){
                    if (gameState.at(row,col,layer)==whoIAm){
                        me[row]++;
                    }
                    if (gameState.at(row,col,layer)==opponent){
                        opp[row]++;
                    }
                }
            }
        }
        
        //Evaluate marks along each column for each layer (16)
        for(int layer=0; layer<boardSize; layer++){
            for(int col=0; col<boardSize; col++){
                for(int row=0; row<boardSize; row++){
                    if(gameState.at(row, col, layer)==whoIAm){
                        me[16+col]++;
                    }
                    if(gameState.at(row, col, layer)==opponent){
                        opp[16+col]++;
                    }
                }

            }
        }
        
        //Evaluate each pile (16)
        for(int row=0; row<boardSize; row++){
            for (int col=0; col<boardSize; col++){
                for(int layer=0; layer<boardSize; layer++){
                    if(gameState.at(row, col, layer)==whoIAm){
                        me[32+layer]++;
                    }
                    if(gameState.at(row, col, layer)==opponent){
                        opp[32+layer]++;
                    }
                }
            }
        }
        
        //Evaluate case 2: marks along any of the two diagonals for each layer (8) 
        for(int layer=0; layer<boardSize; layer++){  
            for (int d=0; d<boardSize; d++){
                if (gameState.at(d,d,layer)==whoIAm){
                    me[48+d]++;
                }
                if (gameState.at(d,d,layer)==opponent){
                    opp[48+d]++;
                }
            }
        }
        
        
        for(int layer=0; layer<boardSize; layer++){
            for (int d=0; d<boardSize; d++){
                if (gameState.at(d,(boardSize-d-1),layer)==whoIAm){
                    me[52+d]++;
                }
                if (gameState.at(d,(boardSize-d-1),layer)==opponent){
                    opp[52+d]++;
                }
            }
        }
        
        //Evaluate marks along any of the two diagonals for each row (8)
        for(int row=0; row<boardSize; row++){
            for(int d=0; d<boardSize; d++){
                if(gameState.at(d, row, d)==whoIAm){
                    me[56+d]++;
                }
                if(gameState.at(d, row, d)==opponent){
                    opp[56+d]++;
                }
            }
        }
        
        for(int row=0; row<boardSize; row++){
            for(int d=0; d<boardSize; d++){
                if(gameState.at(row, (boardSize-d-1), d)==whoIAm){
                    me[60+d]++;
                }
                if(gameState.at(row, (boardSize-d-1), d)==opponent){
                    opp[60+d]++;
                }
            }
        }
        
        //Evaluate marks along any of the two diagonals for each column (8)
        for(int col=0; col<boardSize; col++){
            for(int d=0; d<boardSize; d++){
                if(gameState.at(d, col, d)==whoIAm){
                    me[64+d]++;
                }
                if(gameState.at(d, col, d)==opponent){
                    opp[64+d]++;
                }
            }
        }
        
        for(int col=0; col<boardSize; col++){
            for(int d=0; d<boardSize; d++){
                if(gameState.at((boardSize-d-1), col, d)==whoIAm){
                    me[68+d]++;
                }
                if(gameState.at((boardSize-d-1), col, d)==opponent){
                    opp[68+d]++;
                }
            }
        }
        
        //Evaluate marks along any of the 4 main diagonals
        for(int d=0; d<boardSize; d++){
            if(gameState.at(d, d, d)==whoIAm){
                me[72]++;
            }
            if(gameState.at(d, d, d)==opponent){
                opp[72]++;
            }
        }
        
        for(int d=0; d<boardSize; d++){
            if(gameState.at(d, (boardSize-d-1), d)==whoIAm){
                me[73]++;
            }
            if(gameState.at(d, (boardSize-d-1), d)==opponent){
                opp[73]++;
            }
        }
        
        for(int d=0; d<boardSize; d++){
            if(gameState.at((boardSize-d-1), (boardSize-d-1), d)==whoIAm){
                me[74]++;
            }
            if(gameState.at((boardSize-d-1), (boardSize-d-1), d)==opponent){
                opp[74]++;
            }
        }
        
        for(int d=0; d<boardSize; d++){
            if(gameState.at((boardSize-d-1), d, d)==whoIAm){
                me[72]++;
            }
            if(gameState.at((boardSize-d-1), d, d)==opponent){
                opp[72]++;
            }
        }
        
        for(int i=0; i<76; i++){
            sum += valueLine(me[i],opp[i]);
        }
        
        return sum;
    }
    
    public int valueLine(int meIn, int oppIn){
        int result = 0;
        
        if (meIn==0 && oppIn>0){
            result = (int)-Math.pow(10,oppIn);
        }else if(meIn>0 && oppIn==0){
            result = (int)Math.pow(10,meIn);
        }  
        return result;
    }     
}
