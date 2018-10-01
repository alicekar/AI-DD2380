/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Vector;

/**
 *
 * @author alicekarnsund
 */
public class Try {
    private int v;
    private final int playerX = Constants.CELL_X;
    private final int playerO = Constants.CELL_O;
    private int opponent;
    private int whoIam;
    private int nextMove;
    private int depth;
    private final int boardSize = GameState.BOARD_SIZE;
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
        whoIam = gameState.getNextPlayer();
        if(whoIam == playerX){
            opponent = playerO;
        }else{
            opponent = playerX;
        }
        
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }
       
        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        //Random random = new Random();
        //System.out.println(miniMax(gameState, nextPlayer, 4));
        depth = 10;
        nextMove = alphaBeta(gameState, -100000, 100000)[1];
        return nextStates.elementAt(nextMove);
    }    
    
   /**
     * @param state 
     *          the state we are at right now
     * @param alpha
     *          the current best value achievable by me (X)
     * @param beta
     *          the current best value achievable by opponent (O)
     * @return a tuple {v,best move} ie the best next state corresponding to the 
     * v-value
    */
    public int[] alphaBeta(GameState state, int alpha, int beta){  
        Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);       // gives all possible next states
        
        if(depth == 0){    
            v = evaluation(state);
            int[] best = {v,0};
            return(best);
        }
        else{
            depth--;
            if (state.getNextPlayer() == whoIam){
                int[] bestPossible = {-100000,0};      //bestPossible[0]=v
                for(int i = 0; i < nextStates.size(); i++){
                    int[] node = {alphaBeta(nextStates.get(i), alpha, beta)[0], i};
                    if(node[0]>bestPossible[0]){
                        bestPossible = node;
                        if(bestPossible[0]>alpha){
                            alpha = bestPossible[0];
                        }
                    }
                    /**
                    ab = alphaBeta(nextStates.get(i), playerO, alpha, beta)[0];
                    bestPossible[0] = Math.max(ab, bestPossible[0]);
                    int[] node = {bestPossible[0], i};
                    bestPossible = node;
                    alpha = Math.max(alpha, bestPossible[0]);*/

                    if(beta<=alpha){
                        break;                     //Beta-prune
                    }    
                }
                return(bestPossible);
            }

            else {
                int[] bestPossible = {100000, 0};
                for(int i = 0; i < nextStates.size(); i++){
                    int[] node = {alphaBeta(nextStates.get(i), alpha, beta)[0], i};
                    if(node[0]<bestPossible[0]){
                        bestPossible = node;
                        if(bestPossible[0]<beta){
                            beta = bestPossible[0];
                        }
                    }
                    /**
                    ab = alphaBeta(nextStates.elementAt(i), playerX, alpha, beta)[0];
                    bestPossible[0] = Math.min(ab, bestPossible[0]);
                    int[] node = {bestPossible[0], i};
                    bestPossible = node;
                    beta = Math.min(beta, bestPossible[0]);*/

                    if(beta<=alpha){
                        break;                           //alpha-prune
                    }
                }
                return(bestPossible);
            }
        }
    } 
    /**
     * 
     * @param state
     *          current state being analyzed
     * @return a score which tells how useful a certain state is for the player
     * notice that there are 3 different ways in winning the game; 1. 4 marks 
     * along a row, 2. 4 marks alnong a column, 3. 4 marks across any of the
     * two diagonals.
     * But the rewards of X should of course also depend on the moves of O, which
     * valueLine takes care of
     */
    public int evaluation(GameState state){
        int me = 0;
        int sum = 0;
        int opp = 0;
        
        // Evaluate case 1: marks along a row
        for(int row=0; row<boardSize; row++){
            me = 0;
            opp = 0;
            for(int col=0; col<boardSize; col++){
                if(state.at(row,col)==whoIam){
                    me++;
                }
                if(state.at(row, col)==opponent){
                    opp++;
                }
            }
            sum += valueLine(me,opp);
        }
        
        //Evaluate case 2: marks along a column
        for(int col=0; col<boardSize; col++){
            me = 0;
            opp = 0;
            for(int row=0; row<boardSize; row++){
                if(state.at(row, col)==whoIam){
                    me++;
                }
                if(state.at(row, col)==opponent){
                    opp++;
                }
            }
            sum += valueLine(me,opp);
        }
        
        //Evaluate case 3: marks along any of the two diagonals
        me = 0;
        opp = 0;
        for(int d=0; d<boardSize; d++){
            if(state.at(d,d)==whoIam){
                me++;
            }
            if(state.at(d,d)==opponent){
                opp++;
            }
            
        }
        sum += valueLine(me,opp);
        
        me = 0;
        opp = 0;
        for(int d=boardSize-1; d>=0; d--){
            if(state.at(boardSize-1-d,d)==whoIam){
                me++;
            }
            if(state.at(d,d)==opponent){
                opp++;
            }
            
        }
        sum += valueLine(me,opp);
       
        return sum;
    } 
    
    /**
     *
     * @param meIn
     *          # of rewards for me (X)
     * @param oppIn
     *          # of rewards for opp (O)
     * @return the reward
     * The evaluation function should give higher "rewards" depending on 
     * how many marks one have in any of the 3 cases. For example it should tell 
     * the player that 3 in a row is much better than 1 or 2 in the absence 
     * of O, therefor a exponential reward function is choosen instead of a 
     * linear. If O occures on the same row, column or diagonal it should deduct
     * the reward for X.
     */
    public int valueLine(int meIn, int oppIn){
        int result = 0;
        
        if(meIn == 0 || oppIn == 0){
            if(meIn == 0 && oppIn == 0){
                result = 0;
                return result;
            }
            if(oppIn == 0){             //desired!! row/col/diagonal without opp
                result = (int)Math.pow(10, meIn);
                return result;
            }
            else{
                result = (int)-Math.pow(10, oppIn);
                return result;
            }
        }
        else{
            int diff = meIn - oppIn;
            
            if(diff >= 0){
                if(diff == 0){
                    result = 0;
                    return result;
                }
                else{
                    result = (int)-Math.pow(10, diff);
                    return result;
                }
            }
            else{
                result = (int)Math.pow(10, -diff);
                return result;
            }
        }
        
    }
    
    /**
    public int evaluation(int player, GameState state){
        if(state.isXWin()){
            return 1;
        }
        if(state.isOWin()){
            return -1;
        }
        else{
            return 0;
        }   

    }*/
    
} 
        


