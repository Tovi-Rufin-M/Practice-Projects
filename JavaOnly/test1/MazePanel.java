import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public class MazePanel extends JPanel {
    private static final int GRID_SIZE = 30;

    private int[][] grid;
    private MazeGenerator generator;
    private Point startPoint;
    private Point endPoint;
    private Point playerPos;
    private List<Point> solutionPath;
    private Set<Point> solutionSet;

    private boolean showSolutionPath = true;
    private boolean gameWon = false;
    private int moveCount = 0;

    public MazePanel() {
        setPreferredSize(new Dimension(1024, 720));
        setBackground(new Color(12, 16, 23)); // Dark blueprint background
        setFocusable(true);

        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    private void initGame() {
        generator = new MazeGenerator(GRID_SIZE, GRID_SIZE);
        grid = generator.getGrid();

        startPoint = new Point(1, 1);
        endPoint = new Point(GRID_SIZE - 2, GRID_SIZE - 2);
        playerPos = new Point(startPoint.x, startPoint.y);

        solutionPath = MazeSolver.solve(grid, startPoint, endPoint);
        solutionSet = new HashSet<>(solutionPath);

        gameWon = false;
        moveCount = 0;
        repaint();
    }

    private void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_R) {
            initGame();
            return;
        }

        if (keyCode == KeyEvent.VK_P) {
            showSolutionPath = !showSolutionPath;
            repaint();
            return;
        }

        if (gameWon) return;

        int dx = 0, dy = 0;
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                dy = -1;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                dy = 1;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                dx = -1;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                dx = 1;
                break;
            default:
                return;
        }

        int targetX = playerPos.x + dx;
        int targetY = playerPos.y + dy;

        if (targetX >= 0 && targetX < GRID_SIZE && targetY >= 0 && targetY < GRID_SIZE) {
            if (grid[targetY][targetX] == 0) {
                playerPos.setLocation(targetX, targetY);
                moveCount++;

                if (playerPos.equals(endPoint)) {
                    gameWon = true;
                }
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dynamic Layout Calculations
        int desiredHudWidth = Math.max(220, Math.min(300, (int) (panelWidth * 0.25)));
        int availableMazeW = Math.max(100, panelWidth - desiredHudWidth - 35);
        int availableMazeH = Math.max(100, panelHeight - 30);

        int tileSize = Math.max(3, Math.min(availableMazeW / GRID_SIZE, availableMazeH / GRID_SIZE));

        int gridPixelW = GRID_SIZE * tileSize;
        int gridPixelH = GRID_SIZE * tileSize;

        int offsetX = 15 + (availableMazeW - gridPixelW) / 2;
        int offsetY = 15 + (availableMazeH - gridPixelH) / 2;

        int hudX = offsetX + gridPixelW + 15;
        int hudY = offsetY;
        int actualHudWidth = Math.max(180, panelWidth - hudX - 15);
        int hudHeight = gridPixelH;

        // 1. Render 50x50 Tiles
        renderTiles(g2d, offsetX, offsetY, tileSize);

        // 2. Render Solution Path Overlay (if enabled)
        if (showSolutionPath && solutionPath != null) {
            renderSolutionPath(g2d, offsetX, offsetY, tileSize);
        }

        // 3. Render Entrance, Exit & Player
        renderEntities(g2d, offsetX, offsetY, tileSize);

        // 4. Render Dynamic Side Dev HUD Panel
        renderDevHUD(g2d, hudX, hudY, actualHudWidth, hudHeight, tileSize);
    }

    private void renderTiles(Graphics2D g, int offsetX, int offsetY, int tileSize) {
        Color wallFill = new Color(24, 34, 50);
        Color wallBorder = new Color(38, 54, 80);
        Color pathFill = new Color(15, 23, 36);
        Color pathGrid = new Color(22, 33, 50);

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int px = offsetX + x * tileSize;
                int py = offsetY + y * tileSize;

                if (grid[y][x] == 1) {
                    g.setColor(wallFill);
                    g.fillRect(px, py, tileSize, tileSize);
                    if (tileSize >= 6) {
                        g.setColor(wallBorder);
                        g.drawRect(px, py, tileSize - 1, tileSize - 1);
                    }
                } else {
                    g.setColor(pathFill);
                    g.fillRect(px, py, tileSize, tileSize);
                    if (tileSize >= 6) {
                        g.setColor(pathGrid);
                        g.drawRect(px, py, tileSize - 1, tileSize - 1);
                    }
                }
            }
        }
    }

    private void renderSolutionPath(Graphics2D g, int offsetX, int offsetY, int tileSize) {
        g.setColor(new Color(255, 214, 0, 150)); // Golden yellow dev trace
        int inset = Math.max(1, tileSize / 4);
        int size = Math.max(1, tileSize - inset * 2);

        for (Point pt : solutionPath) {
            if (pt.equals(startPoint) || pt.equals(endPoint)) continue;
            int px = offsetX + pt.x * tileSize;
            int py = offsetY + pt.y * tileSize;

            g.fillRect(px + inset, py + inset, size, size);
        }
    }

    private void renderEntities(Graphics2D g, int offsetX, int offsetY, int tileSize) {
        // Entrance (Start)
        int sx = offsetX + startPoint.x * tileSize;
        int sy = offsetY + startPoint.y * tileSize;
        g.setColor(new Color(0, 230, 118));
        g.fillRect(sx + 1, sy + 1, Math.max(1, tileSize - 2), Math.max(1, tileSize - 2));

        // Exit (End)
        int ex = offsetX + endPoint.x * tileSize;
        int ey = offsetY + endPoint.y * tileSize;
        g.setColor(new Color(255, 61, 0));
        g.fillRect(ex + 1, ey + 1, Math.max(1, tileSize - 2), Math.max(1, tileSize - 2));

        // Player
        int plx = offsetX + playerPos.x * tileSize;
        int ply = offsetY + playerPos.y * tileSize;
        g.setColor(new Color(0, 229, 255));
        g.fillRect(plx + 1, ply + 1, Math.max(1, tileSize - 2), Math.max(1, tileSize - 2));
        if (tileSize >= 8) {
            g.setColor(Color.WHITE);
            g.drawRect(plx + 2, ply + 2, Math.max(1, tileSize - 5), Math.max(1, tileSize - 5));
        }
    }

    private void renderDevHUD(Graphics2D g, int hudX, int hudY, int hudWidth, int hudHeight, int tileSize) {
        // Panel Background & Border
        g.setColor(new Color(18, 26, 38));
        g.fillRect(hudX, hudY, hudWidth, hudHeight);

        g.setColor(new Color(40, 60, 90));
        g.drawRect(hudX, hudY, hudWidth, hudHeight);

        // Header Section
        int headerH = Math.min(50, Math.max(35, hudHeight / 14));
        g.setColor(new Color(25, 36, 52));
        g.fillRect(hudX + 1, hudY + 1, hudWidth - 2, headerH);

        g.setFont(new Font("Consolas", Font.BOLD, 11));
        g.setColor(new Color(0, 229, 255));
        g.drawString("MAZE_ENGINE v0.5.2", hudX + 12, hudY + 18);
        g.setFont(new Font("Consolas", Font.PLAIN, 10));
        g.setColor(new Color(0, 255, 136));
        g.drawString("[RESIZABLE_DEV_CONSOLE]", hudX + 12, hudY + 34);

        g.setColor(new Color(40, 60, 90));
        g.drawLine(hudX, hudY + headerH + 1, hudX + hudWidth, hudY + headerH + 1);

        int y = hudY + headerH + 20;
        int lineGap = Math.min(20, Math.max(14, hudHeight / 38));

        // SECTION 1: SYSTEM METRICS
        drawSectionHeader(g, "SYSTEM METRICS", hudX + 12, y);
        y += lineGap;
        drawMetric(g, "GRID SIZE", String.format("%dx%d", GRID_SIZE, GRID_SIZE), hudX + 12, y);
        y += lineGap;
        drawMetric(g, "TILE SCALE", tileSize + " px", hudX + 12, y);
        y += lineGap;
        drawMetric(g, "MAP SEED", "#" + generator.getSeed(), hudX + 12, y);
        y += (int) (lineGap * 1.4);

        // SECTION 2: PLAYER STATE
        drawSectionHeader(g, "PLAYER STATE", hudX + 12, y);
        y += lineGap;
        drawMetric(g, "POSITION", String.format("(%02d, %02d)", playerPos.x, playerPos.y), hudX + 12, y);
        y += lineGap;
        drawMetric(g, "MOVES", String.valueOf(moveCount), hudX + 12, y);
        y += lineGap;
        drawMetric(g, "STATUS", gameWon ? "SOLVED" : "EXPLORING", hudX + 12, y);
        y += (int) (lineGap * 1.4);

        // SECTION 3: BFS PATHFINDER
        drawSectionHeader(g, "PATHFINDER (BFS)", hudX + 12, y);
        y += lineGap;
        drawMetric(g, "PATH LEN", solutionPath.size() + " Steps", hudX + 12, y);
        y += lineGap;
        drawMetric(g, "TRACE", showSolutionPath ? "ACTIVE" : "HIDDEN", hudX + 12, y);
        y += (int) (lineGap * 1.4);

        // SECTION 4: DEV CONTROLS
        drawSectionHeader(g, "DEV CONTROLS", hudX + 12, y);
        y += lineGap;
        drawControlKey(g, "[WASD/ARROWS]", "Move", hudX + 12, y);
        y += lineGap;
        drawControlKey(g, "[P]", "Toggle Path", hudX + 12, y);
        y += lineGap;
        drawControlKey(g, "[R]", "Re-Gen Map", hudX + 12, y);
        y += (int) (lineGap * 1.4);

        // SECTION 5: LEGEND
        drawSectionHeader(g, "TILE LEGEND", hudX + 12, y);
        y += lineGap;
        drawLegendItem(g, new Color(0, 230, 118), "Entrance (1,1)", hudX + 12, y);
        y += lineGap;
        drawLegendItem(g, new Color(255, 61, 0), "Exit (49,49)", hudX + 12, y);
        y += lineGap;
        drawLegendItem(g, new Color(0, 229, 255), "Player", hudX + 12, y);
        y += lineGap;
        drawLegendItem(g, new Color(255, 214, 0), "BFS Trace", hudX + 12, y);

        // VICTORY NOTIFICATION BANNER
        if (gameWon) {
            y += (int) (lineGap * 1.6);
            int bannerH = Math.min(60, hudHeight - (y - hudY) - 10);
            if (bannerH >= 30) {
                g.setColor(new Color(0, 255, 136, 40));
                g.fillRect(hudX + 8, y, hudWidth - 16, bannerH);
                g.setColor(new Color(0, 255, 136));
                g.drawRect(hudX + 8, y, hudWidth - 16, bannerH);

                g.setFont(new Font("Consolas", Font.BOLD, 12));
                g.drawString(">> MAZE_SOLVED! <<", hudX + 16, y + 18);
                if (bannerH >= 45) {
                    g.setFont(new Font("Consolas", Font.PLAIN, 10));
                    g.setColor(Color.WHITE);
                    g.drawString("Press [R] to Reset Map", hudX + 16, y + 34);
                }
            }
        }
    }

    private void drawSectionHeader(Graphics2D g, String title, int x, int y) {
        g.setFont(new Font("Consolas", Font.BOLD, 15));
        g.setColor(new Color(0, 229, 255));
        g.drawString("--- " + title + " ---", x, y);
    }

    private void drawMetric(Graphics2D g, String label, String value, int x, int y) {
        g.setFont(new Font("Consolas", Font.PLAIN, 15));
        g.setColor(new Color(140, 170, 205));
        g.drawString(label + ":", x, y);
        g.setColor(Color.WHITE);
        g.drawString(value, x + 82, y);
    }

    private void drawControlKey(Graphics2D g, String key, String action, int x, int y) {
        g.setFont(new Font("Consolas", Font.BOLD, 15));
        g.setColor(new Color(0, 255, 136));
        g.drawString(key, x, y);
        g.setFont(new Font("Consolas", Font.PLAIN, 15));
        g.setColor(new Color(180, 200, 225));
        g.drawString(action, x + 90, y);
    }

    private void drawLegendItem(Graphics2D g, Color color, String label, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y - 8, 9, 9);
        g.setColor(Color.WHITE);
        g.drawRect(x, y - 8, 9, 9);

        g.setFont(new Font("Consolas", Font.PLAIN, 15 ));
        g.setColor(new Color(180, 200, 225));
        g.drawString(label, x + 16, y);
    }
}
