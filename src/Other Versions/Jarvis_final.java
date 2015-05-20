import reversi.*;

import java.util.TreeSet;
import java.util.Vector;


public class Jarvis implements ReversiAlgorithm
{
	// Initialization
	
    //Constants
    private final static int DEPTH_LIMIT = 15;
    //Variables
    boolean initialized;
    volatile boolean running; // Note: volatile for synchronization issues.
    GameController controller;
    GameState initialState;
    int myIndex;
    int opIndex;
    Move selectedMove;
    int selectedMoveScore;
	// score matrix - weights for different positions
    int scoreMatrix[][] = {{10000,-3000,1000,800,800,1000,-3000,10000},
                            {-3000,-5000,-450,-500,-500,-450,-5000,-3000},
                            {1000,-450,30,10,10,30,-450,1000},
                            {800,-500,10,50,50,10,-500,800},
                            {800,-500,10,50,50,10,-500,800},
                            {1000,-450,30,10,10,30,-450,1000},
                            {-3000,-5000,-450,-500,-500,-450,-5000,-3000},
                            {10000,-3000,1000,800,800,1000,-3000,10000}};

	// Constructor
	
    public Jarvis() {}

	// Request move
	
    public void requestMove(GameController requester)
    {
        running = false;
        requester.doMove(selectedMove);
    }

	// Initialization
	
    public void init(GameController game, GameState state, int playerIndex, int turnLength)
    {
        initialState = state;	//setting current state to initial state (for depth searching)
        myIndex = playerIndex;
        opIndex = (playerIndex + 1) % 2;	//indexes can be only 0 or 1
        controller = game;
        initialized = true;
        selectedMoveScore = Integer.MIN_VALUE;	//initial score value
    }

	// Get name of engine
	
    public String getName() { return "jarvis"; }

	// Cleanup
	
    public void cleanup() {}

	// Run
	
    public void run()
	{
        //Implementation of the actual algorithm
		
        while(!initialized);
        initialized = false;
        running = true;
        selectedMove = null;
        System.out.println("Jarvis searching ....");

        int currentDepth = 1;

        while (running)
        {
			// Search for next move from tree (alpha-beta pruning)
            Move newMove = searchToDepth(currentDepth++);

            // Check if there's a new move available.
            if (newMove != null)
            {
                selectedMove = newMove;
            }
        }

        if (running) // Make a move if there is still time left.
        {
            controller.doMove(selectedMove);
            System.out.print("Selected move : ");
            System.out.print(selectedMove.getX());
            System.out.print(",");
            System.out.println(selectedMove.getY());
        }
    }

	// Depth search function
	
    private Move searchToDepth(int depth) 
	{
        //System.out.println(depth);
        selectedMoveScore = Integer.MIN_VALUE;
        Vector<Move> availableMoves = initialState.getPossibleMoves(myIndex);	//all possible moves
        int maxScore = Integer.MIN_VALUE;
        int bestIndex = 0;
        if (availableMoves.isEmpty()) return new Move(0,0,-1);

        for (int i = 0; i < availableMoves.size(); ++i) 
		{
            Move move = availableMoves.elementAt(i);
			
			// If one of the available moves is corner move, take it.
			// Because corners are the most strategically important positions
            if(checkCorner(move))
			{
                running = false;
                return move;
            }
			
			// Calculate node score based on Alpha-Beta approach
            int childScore = alphaBetaMinimax(initialState.getNewInstance(move), move, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, opIndex, depth);
            // Comparison with maxscore
			if(maxScore < childScore)
			{
				// @Pablo : Shouldn't we set childscore to maxscore inside this condition
                bestIndex = i;
				maxScore = childScore;
				//System.out.println(maxScore +" " +depth);
            }
			//System.out.println(running);
        }
        return availableMoves.elementAt(bestIndex);
    }

	// Check corner move function
	
    private boolean checkCorner(Move move) 
	{
        int x  = move.getX();
        int y = move.getY();
        return (x == 0 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 0) || ( x == 7 && y == 7);
    }

	// Alpha - Beta function
	
	//@Pablo : Copy paste the alpha-beta pseudocode you used here. Maybe add the link as well (if you have)
	
    private int alphaBetaMinimax(GameState state, Move m, int alpha, int beta, int depth, int index, int maxDepth)
	{
        if(running && depth < maxDepth)
		//if(depth < maxDepth)
		{
            if (beta <= alpha) 
			{
                return (index == myIndex) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            Vector<Move> availableMoves = state.getPossibleMoves(index);	//get possible moves from the state

            if (availableMoves.isEmpty()) return 0;	//no moves left

            int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;

            for (int i = 0; i < availableMoves.size(); ++i) 
			{
                Move move = availableMoves.elementAt(i);

                int currentScore = 0;

                if (index == myIndex)
				{
                    //System.out.println(state.getNewInstance(move).toString() + " " + move.toString() + '\n' + alpha + " " + beta + " " + depth + " " +  opIndex);
                    currentScore = alphaBetaMinimax(state.getNewInstance(move), move,  alpha, beta, depth + 1, opIndex, maxDepth);
                    maxValue = Math.max(maxValue, currentScore);

                    //Set alpha
                    alpha = Math.max(currentScore, alpha);
                } 
				else if (index == opIndex) 
				{

                    //System.out.println(state.getNewInstance(move) + " " + move.toString() + '\n' + alpha + " " + beta + " " + depth + " " +  myIndex);
                    currentScore = alphaBetaMinimax(state.getNewInstance(move), move, alpha, beta, depth + 1, myIndex, maxDepth);
                    minValue = Math.min(minValue, currentScore);

                    //Set beta
                    beta = Math.min(currentScore, beta);
                }
                
                //If a pruning has been done, don't evaluate the rest of the sibling states
                if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE)
                    break;
            }
			
			// Return appropriate score based on the player's turn
            return (index == myIndex)?maxValue:minValue;
        }
        else
		{
			// Calculate score for move
            return calculateScore(m, state);
        }
    }

	// Calculate score function
	
    private int calculateScore(Move m, GameState state)
	{
		// Return weight of the move position
		int myMoveCount = state.getPossibleMoveCount(myIndex);
		int opMoveCount = state.getPossibleMoveCount(opIndex);
		int myMarkCount = state.getMarkCount(myIndex);
		int opMarkCount = state.getMarkCount(opIndex);
		int mobilityValue = 0;
		
		
		if(myMoveCount == 0)
			return Integer.MIN_VALUE;
		
		mobilityValue = 200 * (myMoveCount - opMoveCount) / (myMoveCount + opMoveCount);
		
		int coinsParity = 1000 * (myMarkCount - opMarkCount ) / (myMarkCount + opMarkCount);

        return scoreMatrix[m.getX()][m.getY()] + coinsParity + mobilityValue;
    }

}