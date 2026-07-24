import java.util.ArrayList;
import java.util.List;

public class Lyrics {
    private String title;
    private String artist;
    private Object[][] lyrics;

    public static class WordTiming {
        private String text;
        private int delayMs;          // Delta duration for this word
        private long startTimeMs;     // Cumulative start time from song beginning
        private long endTimeMs;       // Cumulative end time from song beginning
        private boolean invertChunk;  // Precomputed color inversion flag

        public WordTiming(String text, int delayMs, long startTimeMs, long endTimeMs, boolean invertChunk) {
            this.text = text;
            this.delayMs = delayMs;
            this.startTimeMs = startTimeMs;
            this.endTimeMs = endTimeMs;
            this.invertChunk = invertChunk;
        }

        public String getText() {
            return text;
        }

        public int getDelayMs() {
            return delayMs;
        }

        public long getStartTimeMs() {
            return startTimeMs;
        }

        public long getEndTimeMs() {
            return endTimeMs;
        }

        public boolean isInvertChunk() {
            return invertChunk;
        }
    }

    public Lyrics(String title, String artist) {
        this.title = title;
        this.artist = artist;
        this.lyrics = new Object[][]{
            // [Intro: Broken Toy Airplanes]
            //{"A-B-C's", 0.9, " and ", 0.5, "one-two-threes", 1.6},
            //{"You ", 0.6, "and ", 0.5, "me ", 0.4, "are ", 0.5, "meant to be", 1.5},
            //{"Four, ", 0.4, "five, ", 0.4, "six, ", 0.6, "is this a trick?", 2.2},
            //{"I'm ", 0.4, "not ", 0.4, "sure ", 0.4, "but ", 0.4, "I will stick with--", 2.6},
            //{".", 2.0, "..", 2.0, "...", 2.0},

            // [Verse: Broken Toy Airplanes]
            {"Light ", 0.6, "blue eyes ", 0.8, "didn't show ", 0.8, "surprise", 0.9},
            {"When I ", 0.6, "explained ", 0.3, "the fact ", 0.7, "that I'm ", 0.9, "satisfied", 1.1},
            {"The butterflies ", 1.3, "move in ", 0.8, "my tummy", 1.6},
            {"Float around ", 0.8, "and make me ", 0.6, "feel really ", 0.6, "funny", 1.3},
            {"You disagree ", 1.0, "with my ", 0.8, "self-esteem", 1.6},
            {"Did I mention ", 1.0, "you were ", 0.6, "in ", 0.4, "my ", 0.5, "dream?", 0.7},
            {"We ", 0.5, "could ", 0.5, "walk ", 0.4, "on the ", 0.4, "ceiling", 1.9},
            {"And we ", 0.5, "thought that ", 0.8, "nothing ", 0.6, "would ", 0.5, "go ", 0.8, "wrong", 6.3},

            // [Pre-Chorus: Broken Toy Airplanes]
            {"Would you ", 1.0, "be ", 0.8, "so ", 0.8, "kind ", 0.8, "as to play ", 2.0, "along?", 3.0},

            // [Chorus: Broken Toy Airplanes]
            {"A-B-C's ", 0.8, "and ", 0.5, "one-two-threes", 1.5},
            {"You ", 0.6, "and ", 0.5, "me ", 0.4, "are ", 0.5, "meant to be", 1.5},
            {"Four, ", 0.8, "five, ", 0.5, "six, ", 0.4, "is this a trick?", 1.5},
            {"I'm ", 0.5, "not ", 0.5, "sure ", 0.4, "but ", 0.4, "I will stick ", 1.0, "with you", 7.0},
            {"I will ", 0.6, "stick ", 0.5, "with you", 3.2},
            {"I will ", 0.6, "stick ", 0.5, "with you", 3.0}
        };
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Object[][] getLyrics() {
        return lyrics;
    }

    public Object[][] getRawLyrics() {
        return lyrics;
    }

    public List<List<WordTiming>> getParsedLyrics() {
        List<List<WordTiming>> parsed = new ArrayList<>();
        long currentCumulativeTime = 0;

        for (Object[] row : lyrics) {
            List<WordTiming> lineWords = new ArrayList<>();

            StringBuilder sb = new StringBuilder();
            for (Object elem : row) {
                if (elem instanceof String) {
                    sb.append((String) elem);
                }
            }
            String fullLine = sb.toString().trim();
            boolean isInvertLine = fullLine.contains("You and me are meant to be")
                                || fullLine.contains("I'm not sure but I will stick with")
                                || fullLine.contains("Light blue eyes didn't show surprise")
                                || fullLine.contains("Did I mention you were in my dream?")
                                || fullLine.contains("Would you be so kind as to play along?");

            int wordIndexInLine = 0;
            for (int i = 0; i < row.length; i++) {
                if (row[i] instanceof String) {
                    String wordText = (String) row[i];
                    int delayMs = 1200; // Default line pause if last word has no number

                    if (i + 1 < row.length && row[i + 1] instanceof Number) {
                        Number num = (Number) row[i + 1];
                        delayMs = (int) (num.doubleValue() * 1000);
                        i++; // skip delay element
                    }

                    long startTime = currentCumulativeTime;
                    long endTime = currentCumulativeTime + delayMs;
                    currentCumulativeTime = endTime;

                    boolean isSatisfied = wordText.trim().equalsIgnoreCase("satisfied") || wordText.trim().equalsIgnoreCase("meant to be");
                    boolean shouldInvert = (isInvertLine && (wordIndexInLine % 2 == 1)) || isSatisfied;
                    
                    lineWords.add(new WordTiming(wordText, delayMs, startTime, endTime, shouldInvert));
                    wordIndexInLine++;
                }
            }
            parsed.add(lineWords);
        }
        return parsed;
    }
}