import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MazeSolver {

    public static List<Point> solve(int[][] grid, Point start, Point end) {
        int height = grid.length;
        int width = grid[0].length;

        boolean[][] visited = new boolean[height][width];
        Map<Point, Point> parentMap = new HashMap<>();
        Queue<Point> queue = new LinkedList<>();

        queue.add(start);
        visited[start.y][start.x] = true;

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        boolean found = false;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                found = true;
                break;
            }

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (grid[ny][nx] == 0 && !visited[ny][nx]) {
                        visited[ny][nx] = true;
                        Point next = new Point(nx, ny);
                        parentMap.put(next, current);
                        queue.add(next);
                    }
                }
            }
        }

        List<Point> path = new ArrayList<>();
        if (found) {
            Point curr = end;
            while (curr != null) {
                path.add(curr);
                curr = parentMap.get(curr);
            }
            Collections.reverse(path);
        }
        return path;
    }
}
