import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
    private final int width;
    private final int height;
    private final int[][] grid; // 1 = Wall, 0 = Path
    private final Random random;
    private final long seed;

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
        this.seed = System.currentTimeMillis() % 100000;
        this.random = new Random(seed);
        generateMaze();
    }

    public MazeGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
        this.seed = seed;
        this.random = new Random(seed);
        generateMaze();
    }

    private void generateMaze() {
        // Initialize all as walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = 1;
            }
        }

        // Start carving from (1, 1)
        carve(1, 1);

        // Ensure Entrance and Exit are open paths
        grid[1][1] = 0;
        grid[1][2] = 0;
        grid[2][1] = 0;

        grid[height - 2][width - 2] = 0;
        grid[height - 2][width - 3] = 0;
        grid[height - 3][width - 2] = 0;
    }

    private void carve(int cx, int cy) {
        grid[cy][cx] = 0;

        int[][] directions = {
            {0, -2}, // Up
            {0, 2},  // Down
            {-2, 0}, // Left
            {2, 0}   // Right
        };

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 4; i++) indices.add(i);
        Collections.shuffle(indices, random);

        for (int i : indices) {
            int dx = directions[i][0];
            int dy = directions[i][1];
            int nx = cx + dx;
            int ny = cy + dy;

            if (nx > 0 && nx < width - 1 && ny > 0 && ny < height - 1 && grid[ny][nx] == 1) {
                grid[cy + dy / 2][cx + dx / 2] = 0;
                grid[ny][nx] = 0;
                carve(nx, ny);
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSeed() {
        return seed;
    }
}
