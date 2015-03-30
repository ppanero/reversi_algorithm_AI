import reversi.*;

import java.util.TreeSet;
import java.util.Vector;


public class Jarvis implements ReversiAlgorithm
{
    // Constants
    private final static int DEPTH_LIMIT = 6; // Just an example value.

    // Variables
    boolean initialized;
    volatile boolean running; // Note: volatile for synchronization issues.
    GameController controller;
    GameState initialState;
    int myIndex;
    Move selectedMove;

    public Jarvis() {} //the constructor

    public void requestMove(GameController requester)
    {
        running = false;
        requester.doMove(selectedMove);
    }

    public void init(GameController game, GameState state, int playerIndex, int turnLength)
    {
        initialState = state;
        myIndex = playerIndex;
        controller = game;
        initialized = true;
    }

    public String getName() { return "Jarvis"; }

    public void cleanup() {}

    public void run()
    {
        //implementation of the actual algorithm
        while(!initialized);
        initialized = false;
        running = true;
        selectedMove = null;

        int currentDepth = 1;

        while (running && currentDepth < DEPTH_LIMIT)
        {
            Move newMove = searchToDepth(currentDepth++);

            // Check that there's a new move available.
            if (newMove != null)
                selectedMove = newMove;
        }

        if (running) // Make a move if there is still time left.
        {
            controller.doMove(selectedMove);
        }
    }

    private Move searchToDepth(int depth)
    {
        // - Create the tree of depth d (breadth first, depth first, beam search, alpha beta pruning, ...)
        // - Evaluate the leaf nodes
        // - If you think normal minimax search is enough, call the propagateScore method of the parent node
        //   of each leaf node
        // - Call the getOptimalChild method of the root node
        // - Return the move in the optimal child of the root node
        // - Don't forget the time constraint! -> Stop the algorithm when variable "running" becomes "false"
        //   or when you have reached the maximum search depth.


        Move optimalMove;
        Vector moves = initialState.getPossibleMoves(myIndex);
        Vector balance = new Vector<Integer>(moves.capacity());
        int optimalMoveIndex = 0;
        int currentBestScore = 0;


        int numMoves = initialState.getPossibleMoveCount(myIndex);

        if (moves.size() > 0) {
            for (int i = 0; i < numMoves; ++i) {
                GameState auxGame = initialState.getNewInstance((Move)moves.elementAt(i));
                int aux = auxGame.getMarkCount(myIndex) - auxGame.getMarkCount((myIndex + 1)) % 2;
                balance.add(aux);
                if(currentBestScore < aux) {
                    optimalMoveIndex = i;
                    currentBestScore = aux;
                }
            }
            optimalMove = (Move)moves.elementAt(optimalMoveIndex); // Any movement that just happens to be first.
        }
        else
            optimalMove = null;

        return optimalMove;
    }


    private TreeSet<Move> createTree(int depth) {
        return null;
    }
}
