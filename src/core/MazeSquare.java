package core;

public class MazeSquare {
    public Position position;
    public int size;
    public boolean visited = false;

    // Walls
    public boolean top = true, bottom = true, left = true, right = true;

    public MazeSquare(Position position, int size) {
        this.position = position;
        this.size = size;
    }

    public void markVisited() { visited = true; }
    public boolean isVisited() { return visited; }

    public void draw(Renderer r) {
        r.drawSquare(this);
    }
}