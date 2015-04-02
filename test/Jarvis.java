import reversi.*;

import java.io.IOException;
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
        //System.out.print("Player Index : "); System.out.println(myIndex);
        System.out.println("Jarvis searching ....");

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
        Node parent = new Node();
        parent.setScore(0);
        parent.setState(initialState);
        parent = getNextStage(parent, myIndex, 0, depth);
        Vector children = parent.getChildren();

        for(int i = 0; i < children.size(); ++i){
            ((Node)children.elementAt(i)).propagateScore();
        }

        return parent.getOptimalChild().getMove();
    }

    private Node getNextStage(Node n, int player, int currentDepth, int maxDepth)
    {
    	
    	GameState n_state = n.getState();
    	int oppIndex, w, h, plrCount, oppCount;
    	double coinparity_hvalue, mobility_hvalue, corner_hvalue, hvalue;
    	oppIndex = (myIndex+1)%2;
    	
        if(currentDepth <= maxDepth)
        {
            Vector children = n_state.getPossibleMoves(player);
            if (children.size() > 0) 
            {
                for (int j = 0; j < children.size(); ++j) 
                {
                    Node child = new Node(n_state.getNewInstance((Move) children.get(j)), (Move) children.get(j));
                    
                    GameState child_state = child.getState();
                    w = child_state.getWidth();
                    h = child_state.getHeight();
                    //System.out.print("Board size : "); System.out.print(w);System.out.println(h);
                    
                    // Coin parity
                    plrCount = child_state.getMarkCount(myIndex);
                    oppCount = child_state.getMarkCount(oppIndex);
                    coinparity_hvalue = (plrCount-oppCount) / (plrCount+oppCount);
                    
                    // Mobility value                    
                    plrCount = child_state.getPossibleMoveCount(myIndex);
                    oppCount = child_state.getPossibleMoveCount(oppIndex);
                    if((plrCount+oppCount) != 0)
                    	mobility_hvalue = (plrCount-oppCount) / (plrCount+oppCount);
                    else
                    	mobility_hvalue = 0;
                    
                    // Corner value     
                   // System.out.println(child_state.getMarkAt(w, 0));
                   // System.out.println(child_state.getMarkAt(0, h));
                   // System.out.println(child_state.getMarkAt(w, 0));
                    plrCount = 0;
                    oppCount = 0;
                    if(child_state.getMarkAt(0, 0) != -1)
	                    if(child_state.getMarkAt(0, 0) == myIndex)
	                    	plrCount++;
	                    else
	                    	oppCount++;
                    if(child_state.getMarkAt(w-1, 0) != -1)
    	                    if(child_state.getMarkAt(w, 0) == myIndex)
    	                    	plrCount++;
    	                    else
    	                    	oppCount++;
                   if(child_state.getMarkAt(0, h-1) != -1)
    	                    if(child_state.getMarkAt(0, 0) == myIndex)
    	                    	plrCount++;
    	                    else
    	                    	oppCount++;
                    if(child_state.getMarkAt(w-1, h-1) != -1)
    	                    if(child_state.getMarkAt(0, 0) == myIndex)
    	                    	plrCount++;
    	                    else
    	                    	oppCount++;                    
                    if((plrCount+oppCount) != 0)
                    	corner_hvalue = (plrCount-oppCount) / (plrCount+oppCount);
                    else
                    	corner_hvalue = 0;
                    
                    hvalue = 0.01*coinparity_hvalue + 10.0*mobility_hvalue + 100000.0*corner_hvalue;
                    System.out.println(hvalue);
                    child.setScore(hvalue);

                    n.addChild(getNextStage(child, (myIndex + 1) % 2, currentDepth++, maxDepth));
                }
            } 
            else
            {
                n.setScore(n.getState().getMarkCount(myIndex) - n.getState().getMarkCount((myIndex + 1) % 2));
            }
        }
        
        return n;
    }
}
