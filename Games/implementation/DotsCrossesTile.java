public class DotsCrossesTile extends Tile {
    private Player leftEdge;
    private Player rightEdge;
    private Player topEdge;
    private Player bottomEdge;

    public DotsCrossesTile(int row, int col) {
        super(row, col);

        leftEdge = null;
        rightEdge = null;
        topEdge = null;
        bottomEdge = null;
    }

    public void setEdgeOwner(DotsCrossesTile neighbor, Player p) {
        if (neighbor.getRow() == this.getRow()) {
            // Horizontal edge
            if (neighbor.getColumn() < this.getColumn()) {
                leftEdge = p;
            } else {
                rightEdge = p;
            }
        } else {
            // Vertical edge
            if (neighbor.getRow() < this.getRow()) {
                topEdge = p;
            } else {
                bottomEdge = p;
            }
        }

        if (isOwned()) {
            this.setOwner(p);
        }
    }

    private boolean isOwned() {
        return leftEdge != null && rightEdge != null && topEdge != null && bottomEdge != null;
    }
    
    public void setBorderEdge(String direction, Player p) {
        switch (direction.toLowerCase()) {
            case "up":
                topEdge = p;
                break;
            case "down":
                bottomEdge = p;
                break;
            case "left":
                leftEdge = p;
                break;
            case "right":
                rightEdge = p;
                break;
        }
        
        if (isOwned()) {
            this.setOwner(p);
        }
    }
    
    public Player getLeftEdge() {
        return leftEdge;
    }
    
    public Player getRightEdge() {
        return rightEdge;
    }
    
    public Player getTopEdge() {
        return topEdge;
    }
    
    public Player getBottomEdge() {
        return bottomEdge;
    }
    
}
