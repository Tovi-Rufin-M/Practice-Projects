import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String title = "Meant To Be";
            String artist = "Broken Toy Airplanes";
            
            // Define custom location (x, y) and initial base delay
            int posX = 150;
            int posY = 260;
            int delay = 350;

            Lyrics song = new Lyrics(title, artist);
            System.out.println("Title: " + song.getTitle() + ", Artist: " + song.getArtist());
            
            // Safe print loop for console output
            for (Object[] line : song.getLyrics()) {
                for (int i = 0; i < line.length; i++) {
                    if (line[i] instanceof String) {
                        String text = (String) line[i];
                        double wordDelaySec = 1.2; // default if not specified
                        if (i + 1 < line.length && line[i + 1] instanceof Number) {
                            wordDelaySec = ((Number) line[i + 1]).doubleValue();
                            i++; // skip delay element
                        }
                        System.out.println("Text: " + text + " | Delay: " + wordDelaySec + "s");
                    }
                }
            }

            // GUI Window Setup
            JFrame frame = new JFrame(title + " - " + artist + " (Lyrics)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocation(50, 50);
            
            LyricsPanel lyricsPanel = new LyricsPanel(song, posX, posY, delay);
            frame.add(lyricsPanel); 
            frame.setVisible(true);
        });
    }
}