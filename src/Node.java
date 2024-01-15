public class Node {
    Node parent;
    public int col;
    public int row;
    protected int gCost; //dist between current position and start node
    protected int hCost; //dist between cur position and goal
    protected int fCost; //total cost
    protected boolean solid;
    protected boolean open;
    protected boolean checked;

    public Node(int row, int col){
        this.row = row;
        this.col = col;
    }
}
