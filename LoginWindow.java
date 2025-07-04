import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.geom.RoundRectangle2D;


/**
 * The LoginWindow class creates a simple login window for a fitness app.
 * It contains a field for username, as well as login and register buttons.
 * Usernames are stored persistently using Java Serialization (in a file).
 * Case-insensitivity is implemented for usernames.
 * Real-time feedback for username availability is provided.
 * After successful registration, the username remains in the text field.
 * Both button and text field sizes are fixed to prevent layout shifts.
 * This class can be compiled and run in BlueJ.
 */
public class LoginWindow extends JFrame implements ActionListener, DocumentListener {

    // GUI components declaration
    private JLabel userLabel;
    private JTextField userText;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel; // For status messages to the user

    // Color definitions based on HEX codes
    private final Color ELECTRIC_BLUE = Color.decode("#007BFF"); // Electric Blue
    private final Color INVIGORATING_GREEN = Color.decode("#28A745"); // Invigorating Green
    private final Color ENERGETIC_ORANGE = Color.decode("#FFC107"); // Energetic Orange
    private final Color BACKGROUND_DARK = Color.decode("#343A40"); // Dark gray
    private final Color TEXT_LIGHT = Color.WHITE;

    // Set to store registered usernames in memory
    // All usernames will be stored in lowercase to ensure case-insensitivity.
    private static Set<String> registeredUsernames = new HashSet<>();

    // Filename for persistent storage
    private static final String FILENAME = "users.ser";

    /**
     * Constructor for the LoginWindow class.
     * Initializes the GUI components, window layout, and loads users from file.
     */
    public LoginWindow() {
        // Window settings
        setTitle("Anmeldung Fitness-App"); // Window title
        setSize(400, 200); // Smaller window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exits the application when the window is closed
        setLocationRelativeTo(null); // Centers the window on the screen
        setResizable(false); // Prevents resizing the window

        // Create a panel to hold all components
        JPanel panel = new JPanel();
        add(panel); // Add panel to the window

        // Set background color of the panel
        panel.setBackground(BACKGROUND_DARK);

        placeComponents(panel); // Place components on the panel

        // Load existing users from the file when the application starts
        loadUsernames();

        // Add DocumentListener to the userText field
        userText.getDocument().addDocumentListener(this);

        setVisible(true); // Make the window visible
    }

    /**
     * Places the GUI components on the given panel.
     * Uses GridBagLayout for flexible arrangement.
     * @param panel The JPanel on which the components are to be placed.
     */
    private void placeComponents(JPanel panel) {
        // Set layout manager to GridBagLayout
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components

        // Username label
        userLabel = new JLabel("Benutzername:");
        userLabel.setForeground(TEXT_LIGHT); // Set text color
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(userLabel, gbc);

        // Username text field
        userText = new JTextField(20); // 20 characters wide (initial hint, not a strict size)
        userText.setBackground(Color.WHITE); // Text field background
        userText.setForeground(Color.BLACK); // Text color in text field
        userText.setCaretColor(Color.BLACK); // Cursor color

        // --- Textfeld-Größe fixieren ---
        // Basierend auf 20 Spalten und der Standardhöhe eines JTextFields
        // Du kannst die Breite manuell anpassen, wenn 20 Spalten nicht visuell passen.
        // Eine Breite von z.B. 200 Pixeln und die Höhe von getPreferredSize().height
        // ist ein guter Startpunkt.
        Dimension textFieldSize = new Dimension(200, userText.getPreferredSize().height); // Feste Breite, Höhe automatisch
        userText.setPreferredSize(textFieldSize);
        userText.setMinimumSize(textFieldSize);
        userText.setMaximumSize(textFieldSize);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE; // Wichtig: Nicht füllen, damit die feste Größe respektiert wird
        gbc.anchor = GridBagConstraints.WEST; // Anker links, damit es nicht in der Zelle zentriert wird
        panel.add(userText, gbc);

        // --- Buttons in eigenem Panel für feste Größenkontrolle ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // FlowLayout for buttons, center aligned, 10px horizontal gap
        buttonPanel.setBackground(BACKGROUND_DARK); // Same background as main panel

        // Bestimme die ideale Größe für die Buttons
        Dimension buttonSize = new Dimension(150, 40); // Beispielgröße: 150 Breite, 40 Höhe

        // Login button
        loginButton = new RoundedButton("Anmelden", ELECTRIC_BLUE, TEXT_LIGHT);
        loginButton.setPreferredSize(buttonSize);
        loginButton.setMinimumSize(buttonSize);
        loginButton.setMaximumSize(buttonSize);
        buttonPanel.add(loginButton);
        loginButton.addActionListener(this);

        // Register button
        registerButton = new RoundedButton("Registrieren", INVIGORATING_GREEN, TEXT_LIGHT);
        registerButton.setPreferredSize(buttonSize);
        registerButton.setMinimumSize(buttonSize);
        registerButton.setMaximumSize(buttonSize);
        buttonPanel.add(registerButton);
        registerButton.addActionListener(this);

        // Füge das buttonPanel zum Hauptpanel hinzu
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; // Spannt beide Spalten ein
        gbc.fill = GridBagConstraints.NONE; // Wichtig: Nicht füllen, damit das buttonPanel die Größe der Buttons respektiert
        gbc.anchor = GridBagConstraints.CENTER; // Zentrieren des buttonPanels in seiner Zelle
        panel.add(buttonPanel, gbc);

        messageLabel = new JLabel(" "); // Ein Leerzeichen, damit es Höhe bekommt
        messageLabel.setForeground(TEXT_LIGHT);

        // Feste Größe setzen
        Dimension messageSize = new Dimension(360, 20); // Breite und Höhe anpassen je nach Textlänge
        messageLabel.setPreferredSize(messageSize);
        messageLabel.setMinimumSize(messageSize);
        messageLabel.setMaximumSize(messageSize);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // Nachricht Label auch nicht füllen
        panel.add(messageLabel, gbc);
    }

    /**
    * Loads registered usernames from the specified file using object deserialization.
    */
    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast
    private void loadUsernames() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            Object obj = ois.readObject();
            if (obj instanceof Set) {
                registeredUsernames = (Set<String>) obj;
                System.out.println("Es wurden " + registeredUsernames.size() + " Benutzer aus '" + FILENAME + "' geladen.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Keine vorhandene Benutzerdatei '" + FILENAME + "' gefunden. Starte mit leerer Liste.");
            // Dies ist normal beim ersten Start der Anwendung
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Benutzer: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler: Klasse des geladenen Objekts nicht gefunden: " + e.getMessage());
        }
    }

    /**
     * Saves the current set of registered usernames to the specified file using object serialization.
     */
    private void saveUsernames() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(registeredUsernames);
            System.out.println("Benutzerdaten in '" + FILENAME + "' gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Benutzer: " + e.getMessage());
        }
    }

    /**
     * Checks username availability and updates messageLabel in real-time.
     */
    private void checkUsernameAvailability() {
        String username = userText.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            messageLabel.setText(""); // Clear when the field is empty
            registerButton.setEnabled(true); // Enable register button
        } else {
            if (registeredUsernames.contains(username)) {
                messageLabel.setText("Benutzername bereits vergeben!");
                messageLabel.setForeground(ENERGETIC_ORANGE);
                registerButton.setEnabled(false); // Disable register button
            } else {
                messageLabel.setText("Benutzername verfügbar.");
                messageLabel.setForeground(INVIGORATING_GREEN);
                registerButton.setEnabled(true); // Enable register button
            }
        }
    }

    /**
     * This method is called when an ActionEvent (e.g., button click) occurs.
     * Handles login and registration logic.
     * @param e The ActionEvent object.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userText.getText().trim().toLowerCase();

        if (e.getSource() == loginButton) {
            // Logic for the login button
            if (username.isEmpty()) {
                messageLabel.setText("Bitte Benutzername eingeben.");
                messageLabel.setForeground(ENERGETIC_ORANGE);
            } else {
                if (registeredUsernames.contains(username)) {
                    messageLabel.setText("Anmeldung erfolgreich für '" + userText.getText().trim() + "'!"); // Anzeige des originalen Namens
                    messageLabel.setForeground(INVIGORATING_GREEN);

                    this.dispose(); // Close the current login window
                    new FitnessAppGUI(username); // Create and open the new main window (assuming it exists)

                } else {
                    messageLabel.setText("Benutzername nicht gefunden.");
                    messageLabel.setForeground(ENERGETIC_ORANGE);
                }
            }
        } else if (e.getSource() == registerButton) {
            // Logic for the register button
            if (username.isEmpty()) {
                messageLabel.setText("Bitte Benutzername für die Registrierung eingeben.");
                messageLabel.setForeground(ENERGETIC_ORANGE);
            } else if (registeredUsernames.contains(username)) {
                messageLabel.setText("Benutzername existiert bereits. Bitte wählen Sie einen anderen.");
                messageLabel.setForeground(ENERGETIC_ORANGE);
            } else {
                registeredUsernames.add(username); // Add to in-memory set (already lowercase)
                saveUsernames(); // Save the updated set to file

                messageLabel.setText("Registrierung erfolgreich für '" + userText.getText().trim() + "'! Sie können sich jetzt anmelden."); // Anzeige des originalen Namens
                messageLabel.setForeground(INVIGORATING_GREEN);
                // Der Benutzername bleibt im Feld stehen.
                // Der Registrieren-Button bleibt aufgrund des checkUsernameAvailability()
                // Aufrufs (durch den DocumentListener bei eventuellen Änderungen)
                // korrekt aktiviert/deaktiviert. Wenn das Feld nicht geleert wird,
                // bleibt der "Benutzername bereits vergeben!" Status, was korrekt ist,
                // da der Name nun registriert ist.
            }
        }
    }

    // --- Implementierung der DocumentListener-Methoden ---

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkUsernameAvailability();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkUsernameAvailability();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        checkUsernameAvailability();
    }

    /**
     * The main method is the entry point of the application.
     * It creates an instance of the LoginWindow on the Event Dispatch Thread (EDT).
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginWindow();
                }
            });
    }
}

// Die Klasse RoundedButton sollte in einer separaten Datei 'RoundedButton.java' liegen
// oder als statische geschachtelte Klasse definiert werden, wenn sie nur von LoginWindow verwendet wird.
// Für BlueJ-Projekte ist eine separate Datei oft am einfachsten.
class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color textColor;
    private int arcWidth = 20;
    private int arcHeight = 20;

    public RoundedButton(String text, Color bgColor, Color fgColor) {
        super(text);
        this.backgroundColor = bgColor;
        this.textColor = fgColor;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(textColor);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No custom border drawn here
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
        return shape.contains(x, y);
    }
}