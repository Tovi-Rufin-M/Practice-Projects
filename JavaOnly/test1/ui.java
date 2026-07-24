import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

public class UI extends JFrame {

    public UI() {
        this("50x50 Developer Style Maze Game", 1035, 780, new Color(12, 16, 23));
    }

    public UI(String title, int width, int height, Color color) {
        super(title);
        setSize(width, height);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(color);

        MazePanel mazePanel = new MazePanel();
        setLayout(new BorderLayout());
        add(mazePanel, BorderLayout.CENTER);

        pack();
        setVisible(true);

        // Ensure key listener captures focus immediately
        mazePanel.requestFocusInWindow();
    }
}