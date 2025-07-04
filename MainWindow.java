import javax.swing.*;
import java.awt.*;

/**
 * The MainWindow class represents the main application window
 * that appears after a successful login.
 * This version is intentionally empty, providing only the basic window frame.
 */
public class MainWindow extends JFrame {

    // Color definitions (optional - can be removed if not used anywhere)
    private final Color ELECTRIC_BLUE = Color.decode("#007BFF"); // Electric Blue
    private final Color INVIGORATING_GREEN = Color.decode("#28A745"); // Invigorating Green
    private final Color ENERGETIC_ORANGE = Color.decode("#FFC107"); // Energetic Orange
    private final Color BACKGROUND_DARK = Color.decode("#343A40"); // Dark gray
    private final Color TEXT_LIGHT = Color.WHITE;
    /**
     * Constructor for the MainWindow class.
     * Initializes the main application window.
     */
    public MainWindow() {
        // Window settings
        setTitle("Hauptfenster Fitness-App"); // Window title
        setSize(800, 600); // Standardgröße für ein Hauptfenster
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Beendet die Anwendung beim Schließen
        setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm

        // Set background color for the content pane (optional, but good practice for empty windows)
        getContentPane().setBackground( BACKGROUND_DARK );

        // Das Fenster sichtbar machen
        setVisible(true);
    }

    // Die main-Methode ist hier nicht notwendig, da dieses Fenster
    // vom LoginWindow aus aufgerufen wird.
    // Falls du es separat testen möchtest, könntest du sie hinzufügen:
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
    */
}
