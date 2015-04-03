import reversi.*;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Vector;


public class Jarvis implements ReversiAlgorithm
{
    // Constants
    private final static int DEPTH_LIMIT = 10; // Just an example value.

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
        // System.out.print("Player Index : "); System.out.println(myIndex);
        System.out.println("---------------------");
        System.out.println("Jarvis searching ....");
        System.out.println("---------------------");

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
    	double coinparity_hvalue, mobility_hvalue, position_hvalue, move_hvalue, hvalue;
    	oppIndex = (myIndex+1)%2;
    	
        if(currentDepth <= maxDepth)
        {
        	System.out.print("Current Depth :"); System.out.println(currentDepth);
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
                    
                    // Coin parity value
                    
                    /*plrCount = child_state.getMarkCount(myIndex);
                    oppCount = child_state.getMarkCount(oppIndex);
                    coinparity_hvalue = (plrCount-oppCount) / (plrCount+oppCount);*/
                    
                    // Mobility value    
                    
                    /*plrCount = child_state.getPossibleMoveCount(myIndex);
                    oppCount = child_state.getPossibleMoveCount(oppIndex);
                    if((plrCount+oppCount) != 0)
                    	mobility_hvalue = (plrCount-oppCount) / (plrCount+oppCount);
                    else
                    	mobility_hvalue = 0;*/
                    
                    // Positional value
                    
                   /* position_hvalue = 0;
                    int[][] position_wts = { {1000,-300,100,80,80,100,-300,1000},
                    						 {-300,-500,-45,-50,-50,-45,-500,-300},
                    						 {100,-45,3,1,1,3,-45,100},
                    						 {80,-50,1,5,5,1,-50,80},
                    						 {80,-50,1,5,5,1,-50,80},
                    						 {100,-45,3,1,1,3,-45,100},
                    						 {-300,-500,-45,-50,-50,-45,-500,-300},
                    						 {1000,-300,100,80,80,100,-300,1000} };
                    for(int i1=0;i1<w;i1++)
                    {
                    	for(int i2=0;i2<h;i2++)
                    	{
                    		//position_wts is symmetric
                    		if(child_state.getMarkAt(i1,i2) != -1)
                    		{
                    			int sign = (child_state.getMarkAt(i1,i2) == myIndex) ? 1 : 0;
                    			position_hvalue += sign * position_wts[i2][i1];
                    		}
                    	}
                    }*/
                    
                    // Move value
                    
                    move_hvalue = 0;
                    int[][] move_wts = { {1000,-300,100,80,80,100,-300,1000},
                						 {-300,-500,-45,-50,-50,-45,-500,-300},
                						 {100,-45,3,1,1,3,-45,100},
                						 {80,-50,1,5,5,1,-50,80},
                						 {80,-50,1,5,5,1,-50,80},
                						 {100,-45,3,1,1,3,-45,100},
                						 {-300,-500,-45,-50,-50,-45,-500,-300},
                						 {1000,-300,100,80,80,100,-300,1000} };
                    move_hvalue = move_wts[child.getMove().getY()][child.getMove().getX()];
                    

                    // Print board
                    
                    //java.lang.String game_state_str = child_state.toString();
                    //System.out.println("---");
                    //System.out.println(game_state_str);
                    //System.out.println("---");
                    
                   
                    
                   //hvalue = 0.01*coinparity_hvalue + 10.0*mobility_hvalue;
                   //hvalue = position_hvalue;
                   hvalue = move_hvalue; 
                   System.out.println(hvalue);
                   child.setScore(n.getScore()+hvalue);

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
