import reversi.*;
import java.util.Vector;


public class JarvisAB implements ReversiAlgorithm
{
    // Constants
    private final static int DEPTH_LIMIT = 15; // Just an example value.

    // Variables
    boolean initialized;
    volatile boolean running; // Note: volatile for synchronization issues.
    GameController controller;
    GameState initialState;
    int myIndex;
    int opIndex;
    Move selectedMove;
    int selectedMoveScore;
    int scoreMatrix[][] = {{10000,-3000,1000,800,800,1000,-3000,10000},
                            {-3000,-5000,-450,-500,-500,-450,-5000,-3000},
                            {1000,-450,30,10,10,30,-450,1000},
                            {800,-500,10,50,50,10,-500,800},
                            {800,-500,10,50,50,10,-500,800},
                            {1000,-450,30,10,10,30,-450,1000},
                            {-3000,-5000,-450,-500,-500,-450,-5000,-3000},
                            {10000,-3000,1000,800,800,1000,-3000,10000}};

    public JarvisAB() {} //the constructor

    public void requestMove(GameController requester)
    {
        running = false;
        requester.doMove(selectedMove);
    }

    public void init(GameController game, GameState state, int playerIndex, int turnLength)
    {
        initialState = state;
        myIndex = playerIndex;
        opIndex = (playerIndex + 1) % 2;
        controller = game;
        initialized = true;
        selectedMoveScore = Integer.MIN_VALUE;
    }

    public String getName() { return "JarvisAB"; }

    public void cleanup() {}

    public void run()
    {
        //implementation of the actual algorithm
        while(!initialized);
        initialized = false;
        running = true;
        selectedMove = null;
        System.out.println("JarvisAB searching ....");

        int currentDepth = 1;

        while (running && currentDepth < DEPTH_LIMIT)
        {
            Move newMove = searchToDepth(currentDepth++);

            // Check that there's a new move available.
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

    private Move searchToDepth(int depth) {
        //System.out.println(depth);
        selectedMoveScore = Integer.MIN_VALUE;
        Vector<Move> availableMoves = initialState.getPossibleMoves(myIndex);
        int maxScore = Integer.MIN_VALUE;
        int bestIndex = 0;
        if (availableMoves.isEmpty()) return new Move(0,0,-1);

        for (int i = 0; i < availableMoves.size(); ++i) {
            Move move = availableMoves.elementAt(i);
            if(checkCorner(move)){
                running = false;
                return move;
            }
            int childScore = alphaBetaMinimax(initialState.getNewInstance(move), move, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, opIndex, depth);
            if(maxScore < childScore){
                bestIndex = i;
            }
        }
        return availableMoves.elementAt(bestIndex);
    }

    private boolean checkCorner(Move move) {
        int x  = move.getX();
        int y = move.getY();
        return (x == 0 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 0) || ( x == 7 && y == 7);
    }

    private int alphaBetaMinimax(GameState state, Move m, int alpha, int beta, int depth, int index, int maxDepth){
        if(running && depth < maxDepth) {
            if (beta <= alpha) {
                return (index == myIndex) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            Vector<Move> availableMoves = state.getPossibleMoves(index);

            if (availableMoves.isEmpty()) return 0;

            int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;

            for (int i = 0; i < availableMoves.size(); ++i) {
                Move move = availableMoves.elementAt(i);

                int currentScore = 0;

                if (index == myIndex) {
                    //System.out.println(state.getNewInstance(move).toString() + " " + move.toString() + '\n' + alpha + " " + beta + " " + depth + " " +  opIndex);
                    currentScore = alphaBetaMinimax(state.getNewInstance(move), move,  alpha, beta, depth + 1, opIndex, maxDepth);
                    maxValue = Math.max(maxValue, currentScore);

                    //Set alpha
                    alpha = Math.max(currentScore, alpha);
                } else if (index == opIndex) {

                    //System.out.println(state.getNewInstance(move) + " " + move.toString() + '\n' + alpha + " " + beta + " " + depth + " " +  myIndex);
                    currentScore = alphaBetaMinimax(state.getNewInstance(move), move, alpha, beta, depth + 1, myIndex, maxDepth);
                    minValue = Math.min(minValue, currentScore);

                    //Set beta
                    beta = Math.min(currentScore, beta);
                }
                //reset board

                //If a pruning has been done, don't evaluate the rest of the sibling states
                if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE)
                    break;
            }
            return (index == myIndex)?maxValue:minValue;
        }
        else{
            return calculateScore(m);
        }
    }

    private int calculateScore(Move m){
        return scoreMatrix[m.getX()][m.getY()];
    }

}