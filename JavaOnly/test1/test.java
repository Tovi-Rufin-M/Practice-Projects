import javax.swing.SwingUtilities;

public class Test {
    public static void main(String[] args) {
        math math = new math();
        math.text();
        SwingUtilities.invokeLater(() -> {
            new UI();
        });
    }
}
