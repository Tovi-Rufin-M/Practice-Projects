import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class LyricsPanel extends JPanel {
    private Lyrics song;
    private double speedFactor = 1.0;

    private List<List<Lyrics.WordTiming>> parsedLyrics;
    private long totalDurationMs = 0;

    // Cached Derived Fonts for High Performance
    private Font font48;
    private Font font36;
    private Font font16;

    // Custom loaded Minecraft TTF Font
    private static Font baseMinecraftFont = null;

    static {
        try {
            File fontFile = new File("MinecraftFont/Minecraft.ttf");
            if (!fontFile.exists()) {
                fontFile = new File("Minecraft.ttf");
            }
            if (fontFile.exists()) {
                baseMinecraftFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseMinecraftFont);
            }
        } catch (Exception e) {
            System.err.println("Could not load Minecraft.ttf: " + e.getMessage());
        }
    }

    // High precision playback timing tracking
    private long startTimeMs = 0;
    private boolean isPlaying = false;
    private boolean isFinished = false;

    private int currentLineIndex = 0;
    private int currentChunkIndex = 0;
    private long currentSongTimeMs = 0;

    private JButton resetBtn;
    private boolean lastInvertedState = false;
    private Timer animationLoop;

    public LyricsPanel(Lyrics song, int posX, int posY, int baseDelay) {
        this.song = song;
        this.parsedLyrics = song.getParsedLyrics();

        // Pre-cache Derived Fonts once at startup
        this.font48 = buildFont(48f);
        this.font36 = buildFont(36f);
        this.font16 = buildFont(16f);

        // Calculate total song lyric duration
        if (!parsedLyrics.isEmpty()) {
            List<Lyrics.WordTiming> lastLine = parsedLyrics.get(parsedLyrics.size() - 1);
            if (!lastLine.isEmpty()) {
                totalDurationMs = lastLine.get(lastLine.size() - 1).getEndTimeMs();
            }
        }

        setBackground(new Color(15, 23, 42));
        setLayout(new BorderLayout());

        // Reset Button Panel at the bottom
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        southPanel.setOpaque(false);

        resetBtn = new JButton("RESET");
        initResetButton(resetBtn);

        southPanel.add(resetBtn);
        add(southPanel, BorderLayout.SOUTH);

        // Start High-Frequency Sync Loop (60 FPS)
        initAnimationLoop();
        startPlayback();
    }

    private Font buildFont(float size) {
        if (baseMinecraftFont != null) {
            return baseMinecraftFont.deriveFont(Font.BOLD, size);
        }
        return new Font("Consolas", Font.BOLD, (int) size);
    }

    private void initResetButton(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(font16);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyResetButtonStyle(btn, false);

        // Attach Mouse Listener ONCE at initialization
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setContentAreaFilled(true);
                btn.setBackground(lastInvertedState ? new Color(15, 23, 42, 25) : new Color(255, 255, 255, 35));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
            }
        });

        btn.addActionListener(e -> restart());
    }

    private void applyResetButtonStyle(JButton btn, boolean inverted) {
        Color textColor = inverted ? new Color(15, 23, 42) : new Color(248, 250, 252);
        Color borderColor = inverted ? new Color(15, 23, 42, 180) : new Color(248, 250, 252, 180);

        btn.setForeground(textColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2, false),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
    }

    private void initAnimationLoop() {
        animationLoop = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) return;

                long now = System.currentTimeMillis();
                long elapsedRealMs = now - startTimeMs;
                currentSongTimeMs = (long) (elapsedRealMs * speedFactor);

                if (currentSongTimeMs >= totalDurationMs) {
                    isFinished = true;
                    isPlaying = false;
                    currentLineIndex = parsedLyrics.size();
                    currentChunkIndex = 0;
                    repaint();
                    return;
                }

                updateIndicesForSongTime(currentSongTimeMs);
                repaint();
            }
        });
        animationLoop.start();
    }

    private void updateIndicesForSongTime(long songTimeMs) {
        for (int l = 0; l < parsedLyrics.size(); l++) {
            List<Lyrics.WordTiming> line = parsedLyrics.get(l);
            for (int w = 0; w < line.size(); w++) {
                Lyrics.WordTiming wt = line.get(w);
                if (songTimeMs >= wt.getStartTimeMs() && songTimeMs < wt.getEndTimeMs()) {
                    currentLineIndex = l;
                    currentChunkIndex = w;
                    return;
                }
            }
        }
    }

    public void startPlayback() {
        startTimeMs = System.currentTimeMillis();
        isPlaying = true;
        isFinished = false;
    }

    public void restart() {
        currentLineIndex = 0;
        currentChunkIndex = 0;
        isFinished = false;
        startPlayback();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        boolean isInverted = false;
        if (currentLineIndex < parsedLyrics.size()) {
            List<Lyrics.WordTiming> lineWords = parsedLyrics.get(currentLineIndex);
            if (currentChunkIndex < lineWords.size()) {
                Lyrics.WordTiming currentWord = lineWords.get(currentChunkIndex);
                String currentText = currentWord.getText().trim();
                
                isInverted = currentWord.isInvertChunk();

                // Spam Invert Strobe Effect for "wrong" during its 6.3s duration
                if (currentText.equalsIgnoreCase("wrong") || currentText.equalsIgnoreCase("along?") || currentText.equalsIgnoreCase("meant to be")) {
                    long wordElapsed = currentSongTimeMs - currentWord.getStartTimeMs();
                    isInverted = (wordElapsed / 100) % 2 == 1; // Rapid 100ms strobe
                }
            }
        }

        // Only update button style when inversion state changes
        if (isInverted != lastInvertedState) {
            lastInvertedState = isInverted;
            applyResetButtonStyle(resetBtn, isInverted);
        }

        // Background Gradient
        GradientPaint bgGradient = isInverted
            ? new GradientPaint(0, 0, new Color(248, 250, 252), width, height, new Color(221, 214, 254))
            : new GradientPaint(0, 0, new Color(15, 23, 42), width, height, new Color(46, 16, 101));

        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, width, height);

        // Display ONLY the current single word/phrase at a time using pre-cached font
        if (currentLineIndex < parsedLyrics.size()) {
            List<Lyrics.WordTiming> lineWords = parsedLyrics.get(currentLineIndex);
            if (currentChunkIndex < lineWords.size()) {
                Lyrics.WordTiming currentWord = lineWords.get(currentChunkIndex);
                String currentText = currentWord.getText().trim();

                if (!currentText.isEmpty()) {
                    g2d.setFont(font48);
                    FontMetrics fm = g2d.getFontMetrics(font48);

                    int textWidth = fm.stringWidth(currentText);
                    int textHeight = fm.getAscent();

                    int centerX = (width - textWidth) / 2;
                    int centerY = (height + textHeight) / 2 - 20;

                    // Text & Shadow Colors
                    Color shadowColor = isInverted ? new Color(131, 24, 67, 180) : new Color(60, 20, 90, 220);
                    Color textColor = isInverted ? new Color(15, 23, 42) : new Color(248, 250, 252);

                    // Minecraft Style Text Shadow
                    g2d.setColor(shadowColor);
                    g2d.drawString(currentText, centerX + 3, centerY + 3);

                    // Primary Minecraft Text
                    g2d.setColor(textColor);
                    g2d.drawString(currentText, centerX, centerY);
                }
            }
        } else if (isFinished) {
            g2d.setFont(font36);
            FontMetrics fm = g2d.getFontMetrics(font36);
            String endMsg = "♪ SONG COMPLETED ♪";
            int centerX = (width - fm.stringWidth(endMsg)) / 2;
            int centerY = (height + fm.getAscent()) / 2 - 20;

            g2d.setColor(new Color(56, 189, 248));
            g2d.drawString(endMsg, centerX, centerY);
        }
    }
}
