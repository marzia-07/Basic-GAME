import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}