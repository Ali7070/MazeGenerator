import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import core.Maze;
import core.MazeSquare;
import core.Renderer;

public class MazeApp extends Application {

    private static final int SIZE = 20;
    private static final int ROWS = 25;
    private static final int COLS = 25;

    private Maze maze;
    private Canvas canvas;
    private GraphicsContext gc;

    // Player position
    private int playerRow = 0;
    private int playerCol = 0;

    // Keep track of trail
    private boolean[][] trail;

    // Timer
    private int secondsElapsed = 0;
    private Timeline timerTimeline;
    private boolean timerStarted = false;

    @Override
    public void start(Stage primaryStage) {
        // Generate maze
        maze = new Maze(ROWS, COLS, SIZE);
        maze.generate();

        trail = new boolean[ROWS][COLS];

        // Set up canvas
        canvas = new Canvas(COLS * SIZE, ROWS * SIZE + 30); // extra space for timer
        gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // Keyboard controls
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            boolean moved = false;
            switch (code) {
                case UP -> moved = movePlayer(-1, 0);
                case DOWN -> moved = movePlayer(1, 0);
                case LEFT -> moved = movePlayer(0, -1);
                case RIGHT -> moved = movePlayer(0, 1);
            }

            // Start timer on first move
            if (moved && !timerStarted) {
                startTimer();
                timerStarted = true;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Maze Generator with Timer");
        primaryStage.show();

        redrawMaze(); // draw initial maze
    }

    private void startTimer() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            redrawMaze(); // redraw to update timer display
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private boolean movePlayer(int dRow, int dCol) {
        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS) return false;

        MazeSquare current = maze.getGrid()[playerRow][playerCol];
        MazeSquare next = maze.getGrid()[newRow][newCol];

        // Check walls
        if (dRow == -1 && current.top) return false;
        if (dRow == 1 && current.bottom) return false;
        if (dCol == -1 && current.left) return false;
        if (dCol == 1 && current.right) return false;

        // Mark trail
        trail[playerRow][playerCol] = true;

        // Move player
        playerRow = newRow;
        playerCol = newCol;

        redrawMaze();

        // Goal detection
        if (playerRow == ROWS - 1 && playerCol == COLS - 1) {
            if (timerTimeline != null) timerTimeline.stop(); // stop timer
            gc.setFill(Color.rgb(0, 255, 0, 0.3));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            System.out.println("🎉 You reached the goal in " + secondsElapsed + " seconds!");
        }

        return true;
    }

    private void redrawMaze() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Renderer to draw maze and trail
        Renderer renderer = sq -> {
            int x = sq.position.x;
            int y = sq.position.y;
            int s = sq.size;

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            if (sq.top) gc.strokeLine(x, y, x + s, y);
            if (sq.bottom) gc.strokeLine(x, y + s, x + s, y + s);
            if (sq.left) gc.strokeLine(x, y, x, y + s);
            if (sq.right) gc.strokeLine(x + s, y, x + s, y + s);

            int row = y / SIZE;
            int col = x / SIZE;

            if (trail[row][col]) {
                gc.setFill(Color.ORANGE);
                gc.fillRect(x + 2, y + 2, s - 4, s - 4);
            } else if (sq.isVisited()) {
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(x + 1, y + 1, s - 2, s - 2);
            }

            // Start square
            if (sq == maze.getGrid()[0][0]) {
                gc.setFill(Color.LIGHTGREEN);
                gc.fillRect(x + 1, y + 1, s - 2, s - 2);
            }

            // Goal square
            if (sq == maze.getGrid()[ROWS - 1][COLS - 1]) {
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(x + 1, y + 1, s - 2, s - 2);
            }
        };

        maze.draw(renderer);
        drawPlayer();
        drawTimer();
    }

    private void drawPlayer() {
        MazeSquare sq = maze.getGrid()[playerRow][playerCol];
        gc.setFill(Color.RED);
        gc.fillOval(sq.position.x + 2, sq.position.y + 2, sq.size - 4, sq.size - 4);
    }

    private void drawTimer() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Time: " + secondsElapsed + "s", 10, ROWS * SIZE + 20);
    }

    public static void main(String[] args) {
        launch();
    }
}
