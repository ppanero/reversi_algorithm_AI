import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import reversi.GameController;
import reversi.GameState;
import reversi.Move;
import reversi.Node;
import reversi.ReversiAlgorithm;

public class MyAlgorithm
  implements ReversiAlgorithm
{
  private static final int DEPTH_LIMIT = 4;
  boolean initialized;
  volatile boolean running;
  GameController controller;
  GameState initialState;
  int myIndex;
  Move selectedMove;
  Move optimalMove;
  Queue<Node> queue = new LinkedList();
  boolean maximizing_turn;
  Node rootNode;
  
  public void requestMove(GameController paramGameController)
  {
    this.running = false;
    paramGameController.doMove(this.selectedMove);
  }
  
  public void init(GameController paramGameController, GameState paramGameState, int paramInt1, int paramInt2)
  {
    this.initialState = paramGameState;
    this.myIndex = paramInt1;
    this.controller = paramGameController;
    this.initialized = true;
  }
  
  public String getName()
  {
    return "MyAlgorithm";
  }
  
  public void cleanup() {}
  
  public void run()
  {
    while (!this.initialized) {}
    this.initialized = false;
    this.running = true;
    this.selectedMove = null;
    
    int i = 1;
    while ((this.running) && (i < 4))
    {
      Move localMove = searchToDepth(i++);
      if (localMove != null) {
        this.selectedMove = localMove;
      }
    }
    if (this.running) {
      this.controller.doMove(this.selectedMove);
    }
    //System.exit(0);
  }
  
  void printindent(String paramString, int paramInt, boolean paramBoolean)
  {
    for (int i = 0; i < paramInt; i++) {
      System.out.print("\t");
    }
    System.out.print(paramString);
    if (paramBoolean) {
      System.out.println();
    }
  }
  
  void printBoard(GameState paramGameState, int paramInt)
  {
    printindent("game board ", paramInt, true);
    for (int i = 0; i <= 7; i++)
    {
      printindent("", paramInt, false);
      for (int j = 0; j <= 7; j++)
      {
        int k = paramGameState.getMarkAt(i, j);
        if (k == -1) {
          System.out.print("- ");
        } else {
          System.out.print(k + " ");
        }
      }
      System.out.println("\n");
    }
  }
  
  Move searchToDepth(int paramInt)
  {
    this.rootNode = new Node();
    this.rootNode.setState(this.initialState);
    
    Vector localVector1 = this.initialState.getPossibleMoves(this.myIndex);
    if (localVector1.size() == 1) {
      return (Move)localVector1.elementAt(0);
    }
    Vector localVector2 = breadthFirst(paramInt);
    for (int i = 0; i < localVector2.size(); i++)
    {
      Node localNode2 = (Node)localVector2.elementAt(i);
      this.maximizing_turn = checkMinMax(localNode2);
      localNode2.getParent().propagateScore(this.maximizing_turn);
    }
    Node localNode1 = this.rootNode.getOptimalChild();
    if (localNode1 != null)
    {
      this.optimalMove = this.rootNode.getOptimalChild().getMove();
      return this.optimalMove;
    }
    this.optimalMove = null;
    
    return this.optimalMove;
  }
  
  private boolean checkMinMax(Node paramNode)
  {
    if (paramNode.getMove().getPlayer() == 0)
    {
      this.maximizing_turn = true;
      return this.maximizing_turn;
    }
    this.maximizing_turn = false;
    return this.maximizing_turn;
  }
  
  private Vector<Node> breadthFirst(int paramInt)
  {
    int i = 0;
    
    double d = 0.0D;
    Vector localVector1 = new Vector();
    
    this.queue.clear();    
    this.queue.add(this.rootNode);

    printindent("max depth " + paramInt, i, true);
    printindent("at depth " + i, i, true);
    printBoard(this.rootNode.getState(), i);
    while (!this.queue.isEmpty())
    {
      int j;
      if (i % 2 == 0) {
        j = this.myIndex;
      } else if (this.myIndex == 1) {
        j = 0;
      } else {
        j = 1;
      }
      Node localNode1 = (Node)this.queue.poll();
      Vector localVector2 = localNode1.getState().getPossibleMoves(j);

      String str = "root";
      if (!localNode1.equals(this.rootNode)) {
        str = localNode1.getMove().toString();
      }
      printindent("node " + str + " has children " + localVector2.size(), i, true);
      for (int k = 0; k < localVector2.size(); k++)
      {
        GameState localGameState = localNode1.getState().getNewInstance((Move)localVector2.elementAt(k));
        Move localMove = (Move)localVector2.elementAt(k);
        Node localNode2 = new Node(localGameState, localMove);
        localNode1.addChild(localNode2);
        localNode2.setParent(localNode1);        
        d = scoreNode(localNode2);        
        localNode2.setScore(d);
        
        printindent("found a node : " + localMove, i, true);
        if (i == paramInt)
        {
          printindent("adding to leafs, node : " + localMove, i, true);
          
          localVector1.add(localNode2);
        }
        else
        {
          this.queue.add(localNode2);
          
          i++;
		  System.out.println("i : " +i);
        }
      }
    }
    return localVector1;
  }
  
  private double scoreNode(Node paramNode)
  {
    double[][] arrayOfDouble = { { 100.0D, -10.0D, 11.0D, 6.0D, 6.0D, 11.0D, -10.0D, 100.0D }, { -10.0D, -30.0D, 1.0D, 2.0D, 2.0D, 1.0D, -30.0D, -10.0D }, { 10.0D, 1.0D, 5.0D, 4.0D, 4.0D, 5.0D, 1.0D, 10.0D }, { 6.0D, 2.0D, 4.0D, 2.0D, 2.0D, 4.0D, 2.0D, 6.0D }, { 6.0D, 2.0D, 4.0D, 2.0D, 2.0D, 4.0D, 2.0D, 6.0D }, { 10.0D, 1.0D, 5.0D, 4.0D, 4.0D, 5.0D, 1.0D, 10.0D }, { -10.0D, -30.0D, 1.0D, 2.0D, 2.0D, 1.0D, -30.0D, -10.0D }, { 100.0D, -10.0D, 11.0D, 6.0D, 6.0D, 11.0D, -10.0D, 100.0D } };
    Move localMove = paramNode.getMove();
    double d = arrayOfDouble[localMove.getX()][localMove.getY()];
    return d;
  }
}
