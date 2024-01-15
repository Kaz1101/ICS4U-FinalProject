import java.util.ArrayList;

public class PathfindAI {

    private Node[][] nodes;
    private ArrayList<Node> openTile = new ArrayList<>();
    public ArrayList<Node> path = new ArrayList<>();
    private Node start, goal, current;
    private boolean goalReached;
    private int step = 0;
    private int searchLimit = 9000;


    public PathfindAI(){
        nodeSetup();
    }

    public void nodeSetup(){
        nodes = new Node[Setup.rowMax[Setup.curMap]][Setup.colMax[Setup.curMap]];

        int row = 0;
        int col = 0;

        while (col < Setup.colMax[Setup.curMap] && row < Setup.rowMax[Setup.curMap]){
            nodes[row][col] = new Node(row, col);
            col++;

            if (col == Setup.colMax[Setup.curMap]){
                col = 0;
                row++;
            }
        }
        System.out.println("setup ;D");
    }

    public void setNode(int startRow, int startCol, int goalRow, int goalCol){
        nodeReset();

        start = nodes[startRow][startCol];
        current = start;
        goal = nodes[goalRow][goalCol];

        openTile.add(current);

        int row = 0;
        int col = 0;

        while (col < Setup.colMax[Setup.curMap] && row < Setup.rowMax[Setup.curMap]){
            if (Setup.collisionData[Setup.curMap][row][col]){
                nodes[row][col].solid = true;
            }
            findCost(nodes[row][col]);

            col++;
            if(col == Setup.colMax[Setup.curMap]){
                col = 0;
                row++;
            }
        }
        System.out.println("set");
    }

    public void findCost(Node n){
        int xDist = Math.abs(n.col - start.col);
        int yDist = Math.abs(n.row - start.row);
        n.gCost = xDist + yDist;

        xDist = Math.abs(n.col - goal.col);
        yDist = Math.abs(n.row - goal.row);
        n.hCost = xDist + yDist;

        n.fCost = n.gCost + n.hCost;
    }

    public boolean search(){
        while(!goalReached && step < searchLimit){
            int row = current.row;
            int col = current.col;

            current.checked = true;
            openTile.remove(current);

            if (row - 1 >= 0){
                openNode(nodes[row - 1][col]);
            } if (col - 1 >= 0){
                openNode(nodes[row][col - 1]);
            } if (row + 1 < Setup.rowMax[Setup.curMap]){
                openNode(nodes[row + 1][col]);
            } if (col + 1 < Setup.colMax[Setup.curMap]){
                openNode(nodes[row][col + 1]);
            }

            int bestNode = 0;
            int bestNodefCost = 999;

            for (int i = 0; i < openTile.size(); i++){
                if (openTile.get(i).fCost < bestNodefCost){
                    bestNode = i;
                    bestNodefCost = openTile.get(i).fCost;
                } else if (openTile.get(i).fCost == bestNodefCost){
                    if (openTile.get(i).gCost < openTile.get(bestNode).gCost){
                        bestNode = i;
                    }
                }
            }

            if (openTile.isEmpty()){
                break;
            }

            current = openTile.get(bestNode);
            if (current == goal){
                goalReached = true;
                trackPath();
            }
            step++;
        }
        System.out.println("searching");
        return goalReached;
    }

    public void openNode(Node n){
        if (!n.open && !n.checked && !n.solid){
            n.open = true;
            n.parent = current;
            openTile.add(n);
        }
    }

    public void nodeReset(){
        int row = 0;
        int col = 0;

        while (col < Setup.colMax[Setup.curMap] && row < Setup.rowMax[Setup.curMap]){
            nodes[row][col].open = false;
            nodes[row][col].checked = false;
            nodes[row][col].solid = false;
            col++;

            if (col == Setup.colMax[Setup.curMap]){
                col = 0;
                row++;
            }
        }
        openTile.clear();
        path.clear();
        goalReached = false;
        step = 0;
    }

    public void trackPath(){
        Node current = goal;

        while(current != start){
            path.addFirst(current);
            current = current.parent;
        }
    }
}
