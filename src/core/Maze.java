package core;
import java.util.*;

public class Maze {
    private final int rows, cols;
    private final int size;
    private final MazeSquare[][] grid;

    public Maze(int rows, int cols, int size) {
        this.rows = rows;
        this.cols = cols;
        this.size = size;
        grid = new MazeSquare[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new MazeSquare(
                        new Position(c * size, r * size),
                        size
                );
            }
        }
    }

    public void generate() {
        Random rand = new Random();
        Stack<MazeSquare> stack = new Stack<>();

        MazeSquare start = grid[0][0];
        start.markVisited();
        stack.push(start);

        while (!stack.isEmpty()) {
            MazeSquare current = stack.peek();
            List<MazeSquare> neighbors = getUnvisitedNeighbors(current);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                MazeSquare next = neighbors.get(rand.nextInt(neighbors.size()));
                removeWalls(current, next);
                next.markVisited();
                stack.push(next);
            }
        }
    }

    private List<MazeSquare> getUnvisitedNeighbors(MazeSquare sq) {
        List<MazeSquare> list = new ArrayList<>();
        int r = sq.position.y / size;
        int c = sq.position.x / size;

        if (r > 0 && !grid[r - 1][c].isVisited()) list.add(grid[r - 1][c]);
        if (r < rows - 1 && !grid[r + 1][c].isVisited()) list.add(grid[r + 1][c]);
        if (c > 0 && !grid[r][c - 1].isVisited()) list.add(grid[r][c - 1]);
        if (c < cols - 1 && !grid[r][c + 1].isVisited()) list.add(grid[r][c + 1]);

        return list;
    }

    private void removeWalls(MazeSquare a, MazeSquare b) {
        int ax = a.position.x / size;
        int ay = a.position.y / size;
        int bx = b.position.x / size;
        int by = b.position.y / size;

        if (ax == bx && ay > by) { a.top = false; b.bottom = false; }
        if (ax == bx && ay < by) { a.bottom = false; b.top = false; }
        if (ay == by && ax > bx) { a.left = false; b.right = false; }
        if (ay == by && ax < bx) { a.right = false; b.left = false; }
    }

    public MazeSquare[][] getGrid() {
        return grid;
    }

    public void draw(Renderer r) {
        for (MazeSquare[] row : grid) {
            for (MazeSquare sq : row) {
                sq.draw(r);
            }
        }
    }
}
