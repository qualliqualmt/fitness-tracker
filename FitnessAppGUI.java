import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List; // Explicitly import List to avoid ambiguity with java.awt.List

/**
 * The FitnessAppGUI class represents the main application window that opens
 * after a successful login. It now extends JFrame and includes basic GUI
 * components, while retaining its backend logic for user, exercise, and record management.
 */
public class FitnessAppGUI extends JFrame { // Changed class name from FitnessApp to FitnessAppGUI

    private static final String BASE_DIR = "users";
    private String currentLoggedInUser; // To store the username of the logged-in user

    // GUI Components (example, you'll expand this)
    private JLabel welcomeLabel;
    private JButton createExerciseButton;
    private JComboBox<String> exerciseDropdown;
    private JTextArea recordsDisplayArea;
    private JButton addRecordButton;
    private JButton calculateWeightButton;
    private JLabel suggestionLabel;

    /**
     * Constructor for the FitnessAppGUI class.
     * Initializes the main application window and its components.
     * @param username The username of the successfully logged-in user.
     */
    public FitnessAppGUI(String username) { // Changed constructor name
        this.currentLoggedInUser = username; // Store the logged-in username

        // Window settings
        setTitle("MyGym - Willkommen, " + username + "!"); // Dynamic title
        setSize(800, 600); // Set an appropriate size for your fitness app
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exits the application when this window is closed
        setLocationRelativeTo(null); // Centers the window on the screen

        // Create a main panel for the fitness app content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout()); // Use BorderLayout for overall layout
        mainPanel.setBackground(Color.decode("#212529")); // Dark background for the app
        add(mainPanel);

        // --- Top Panel for Welcome Message ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(Color.decode("#212529"));
        welcomeLabel = new JLabel("Willkommen MyGym, " + username + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(welcomeLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel for Functionality ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(Color.decode("#343A40")); // Darker gray for content area
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Exercise Management
        JLabel exLabel = new JLabel("Übung auswählen/erstellen:");
        exLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(exLabel, gbc);

        exerciseDropdown = new JComboBox<>();
        exerciseDropdown.setPreferredSize(new Dimension(200, 30));
        exerciseDropdown.setBackground(Color.WHITE);
        exerciseDropdown.setForeground(Color.BLACK);
        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(exerciseDropdown, gbc);

        createExerciseButton = new RoundedButton("Neue Übung", Color.decode("#007BFF"), Color.WHITE);
        createExerciseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newExName = JOptionPane.showInputDialog(FitnessAppGUI.this, "Name der neuen Übung:"); // Changed reference
                    if (newExName != null && !newExName.trim().isEmpty()) {
                        if (createExercise(currentLoggedInUser, newExName.trim())) {
                            JOptionPane.showMessageDialog(FitnessAppGUI.this, "Übung '" + newExName.trim() + "' erfolgreich erstellt."); // Changed reference
                            updateExerciseDropdown(); // Refresh the dropdown
                        } else {
                            JOptionPane.showMessageDialog(FitnessAppGUI.this, "Fehler: Übung '" + newExName.trim() + "' konnte nicht erstellt werden oder existiert bereits.", "Fehler", JOptionPane.ERROR_MESSAGE); // Changed reference
                        }
                    }
                }
            });
        gbc.gridx = 2; gbc.gridy = 0;
        centerPanel.add(createExerciseButton, gbc);

        // Records Display
        recordsDisplayArea = new JTextArea(10, 40);
        recordsDisplayArea.setEditable(false);
        recordsDisplayArea.setBackground(Color.LIGHT_GRAY);
        recordsDisplayArea.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(recordsDisplayArea);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH; // Allow text area to fill space
        gbc.weightx = 1.0; gbc.weighty = 1.0; // Allow text area to grow
        centerPanel.add(scrollPane, gbc);

        // Add Record Button
        addRecordButton = new RoundedButton("Satz hinzufügen", Color.decode("#28A745"), Color.WHITE);
        addRecordButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedEx = (String) exerciseDropdown.getSelectedItem();
                    if (selectedEx == null || selectedEx.isEmpty()) {
                        JOptionPane.showMessageDialog(FitnessAppGUI.this, "Bitte wählen Sie eine Übung aus oder erstellen Sie eine neue.", "Fehler", JOptionPane.WARNING_MESSAGE); // Changed reference
                        return;
                    }

                    // Prompt for multiple sets in one string
                    String setsInput = JOptionPane.showInputDialog(FitnessAppGUI.this, "Geben Sie Sätze ein (z.B. '8,50;7,55;6,60' für Wdh,Gewicht;Wdh,Gewicht;...):");
                    if (setsInput == null || setsInput.trim().isEmpty()) {
                        return; // User cancelled or entered empty string
                    }

                    List<Integer> repsAndWeights = new ArrayList<>();
                    try {
                        String[] setPairs = setsInput.split(";");
                        for (String pair : setPairs) {
                            String[] parts = pair.split(",");
                            if (parts.length == 2) {
                                repsAndWeights.add(Integer.parseInt(parts[0].trim())); // Reps
                                repsAndWeights.add(Integer.parseInt(parts[1].trim())); // Weight
                            } else {
                                throw new IllegalArgumentException("Ungültiges Satzformat: " + pair);
                            }
                        }

                        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());

                        if (addRecord(currentLoggedInUser, selectedEx, date, repsAndWeights)) {
                            JOptionPane.showMessageDialog(FitnessAppGUI.this, "Satz erfolgreich hinzugefügt.");
                            displayRecords(selectedEx); // Refresh records display
                        } else {
                            JOptionPane.showMessageDialog(FitnessAppGUI.this, "Fehler beim Hinzufügen des Satzes.", "Fehler", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(FitnessAppGUI.this, "Ungültige Eingabe für Wiederholungen oder Gewicht. Bitte nur Zahlen verwenden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(FitnessAppGUI.this, "Fehler im Eingabeformat: " + ex.getMessage() + "\nErwartetes Format: 'Wdh,Gewicht;Wdh,Gewicht'", "Fehler", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FitnessAppGUI.this, "Ein unerwarteter Fehler ist aufgetreten: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(addRecordButton, gbc);

        // Calculate Next Weight Button
        calculateWeightButton = new RoundedButton("Nächstes Gewicht vorschlagen", Color.decode("#FFC107"), Color.BLACK);
        calculateWeightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedEx = (String) exerciseDropdown.getSelectedItem();
                    if (selectedEx == null || selectedEx.isEmpty()) {
                        JOptionPane.showMessageDialog(FitnessAppGUI.this, "Bitte wählen Sie eine Übung aus.", "Fehler", JOptionPane.WARNING_MESSAGE); // Changed reference
                        return;
                    }
                    try {
                        double nextWeight = calculateNextWeight(currentLoggedInUser, selectedEx);
                        suggestionLabel.setText("Vorgeschlagenes Gewicht für " + selectedEx + ": " + nextWeight + " kg");
                        suggestionLabel.setForeground(Color.CYAN);
                    } catch (IllegalArgumentException ex) {
                        suggestionLabel.setText("Fehler: " + ex.getMessage());
                        suggestionLabel.setForeground(Color.RED);
                    }
                }
            });
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(calculateWeightButton, gbc);

        // Suggestion Label
        suggestionLabel = new JLabel(" ");
        suggestionLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(suggestionLabel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Event Listener for Dropdown ---
        exerciseDropdown.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedEx = (String) exerciseDropdown.getSelectedItem();
                    if (selectedEx != null) {
                        displayRecords(selectedEx);
                    } else {
                        recordsDisplayArea.setText(""); // Clear if no exercise selected
                        suggestionLabel.setText(" "); // Clear suggestion
                    }
                }
            });

        // Initialize the exercise dropdown and records display
        updateExerciseDropdown();
        if (exerciseDropdown.getItemCount() > 0) {
            exerciseDropdown.setSelectedIndex(0); // Select the first item
            displayRecords((String) exerciseDropdown.getSelectedItem());
        }

        setVisible(true); // Make the FitnessAppGUI window visible
    }

    /**
     * Updates the exercise dropdown with exercises for the current user.
     */
    private void updateExerciseDropdown() {
        exerciseDropdown.removeAllItems();
        List<String> exercises = listExercises(currentLoggedInUser);
        for (String ex : exercises) {
            exerciseDropdown.addItem(ex);
        }
    }

    /**
     * Displays the records for the selected exercise in the text area.
     * @param exName The name of the exercise.
     */
    private void displayRecords(String exName) {
        recordsDisplayArea.setText(""); // Clear previous records
        List<Record> records = getRecords(currentLoggedInUser, exName);
        if (records.isEmpty()) {
            recordsDisplayArea.setText("Keine Sätze für " + exName + " gefunden.");
        } else {
            for (Record record : records) {
                recordsDisplayArea.append(record.toString() + "\n");
            }
        }
    }

    // --- Backend Logic (from your original FitnessApp) ---
    // Ensure the base directory exists
    static {
        File baseDirFile = new File(BASE_DIR);
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs();
        }
    }

    /**
     * Creates a user directory. This method is now available if needed by other parts of the application.
     * Note: User registration (adding to users.ser) is handled by LoginWindow.
     * @param username The username for which to create a directory.
     * @return true if the directory was created or already exists, false otherwise.
     */
    public static boolean createUser(String username) {
        File userDir = new File(BASE_DIR, username);
        if (userDir.exists()) {
            return false; // Benutzer existiert bereits
        } else {
            return userDir.mkdirs(); // Create the directory
        }
    }

    // Alle Benutzer auflisten (Not directly used in FitnessApp GUI, but kept for completeness)
    public static List<String> listUsers() {
        File base = new File(BASE_DIR);
        String[] users = base.list((dir, name) -> new File(dir, name).isDirectory());
        if (users == null) return new ArrayList<>();
        return Arrays.asList(users);
    }

    // Neue Übung anlegen
    public static boolean createExercise(String username, String exName) {
        File userDir = new File(BASE_DIR, username);
        if (!userDir.exists()) {
            userDir.mkdirs(); // Ensure user directory exists
        }
        File exFile = new File(userDir, exName + ".txt");
        if (exFile.exists()) {
            return false;
        } else {
            try {
                return exFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating exercise file: " + e.getMessage());
                return false;
            }
        }
    }

    // Alle Übungen eines Benutzers auflisten
    public static List<String> listExercises(String username) {
        File userDir = new File(BASE_DIR, username);
        if (!userDir.exists()) {
            return new ArrayList<>(); // Return empty list if user directory doesn't exist
        }
        String[] exercises = userDir.list((dir, name) -> name.endsWith(".txt"));
        if (exercises == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String ex : exercises) {
            result.add(ex.replace(".txt", ""));
        }
        return result;
    }

    // Satz hinzufügen
    public static boolean addRecord(String username, String exName, String date, List<Integer> repsAndWeights) {
        File exFile = new File(BASE_DIR + File.separator + username, exName + ".txt");
        try (FileWriter fw = new FileWriter(exFile, true)) { // true for append mode
            StringBuilder sb = new StringBuilder();
            sb.append(date);
            for (int val : repsAndWeights) sb.append(",").append(val);
            fw.write(sb.toString() + System.lineSeparator());
            return true;
        } catch (IOException e) {
            System.err.println("Error adding record: " + e.getMessage());
            return false;
        }
    }

    // Datensätze auslesen
    public static List<Record> getRecords(String username, String exName) {
        File exFile = new File(BASE_DIR + File.separator + username, exName + ".txt");
        List<Record> records = new ArrayList<>();
        if (!exFile.exists()) {
            return records; // Return empty list if file doesn't exist
        }
        try (BufferedReader br = new BufferedReader(new FileReader(exFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) { // Date, Reps, Weight (at least one set)
                    List<Integer> repsAndWeights = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        repsAndWeights.add(Integer.parseInt(parts[i]));
                    }
                    records.add(new Record(parts[0], repsAndWeights));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading records: " + e.getMessage());
            // Fehler ignorieren, leere Liste zurückgeben
        } catch (NumberFormatException e) {
            System.err.println("Error parsing record data: " + e.getMessage());
        }
        return records;
    }

    // Satz-Klasse
    public static class Record {
        public String date;
        public List<Integer> repsAndWeights;

        public Record(String date, List<Integer> repsAndWeights) {
            this.date = date;
            this.repsAndWeights = repsAndWeights;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(date + " | ");
            for (int i = 0; i < repsAndWeights.size(); i += 2) {
                if (i > 0) sb.append(" | ");
                sb.append("Wdh: ").append(repsAndWeights.get(i));
                if (i + 1 < repsAndWeights.size()) sb.append(", Gewicht: ").append(repsAndWeights.get(i + 1)).append("kg");
            }
            return sb.toString();
        }
    }

    /**
     * Berechnet den Gewichtsvorschlag basierend auf dem Durchschnitt der Wiederholungen.
     *
     * Regeln:
     * - avg > 8  → +5% Gewicht
     * - avg < 5  → -5% Gewicht
     * - avg 5–8 → gleiches Gewicht
     *
     * @param username Benutzername
     * @param exName    Name der Übung
     * @return Gewichtsvorschlag (auch wenn unverändert)
     */
    public static double calculateNextWeight(String username, String exName) {
        List<Record> records = getRecords(username, exName);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("Keine Sätze vorhanden für diese Übung.");
        }

        // Get the last recorded weight for the calculation
        int currentWeight = 0;
        List<Integer> lastRepsAndWeights = records.get(records.size() - 1).repsAndWeights;
        if (lastRepsAndWeights.size() >= 2) { // Ensure there are at least reps and weight for the first set
            currentWeight = lastRepsAndWeights.get(1); // The weight is the second element (index 1) of the first set
        } else {
            throw new IllegalArgumentException("Kein Gewicht im letzten Satz gefunden.");
        }

        // Collect reps from all sets of all records
        List<Integer> allReps = new ArrayList<>();
        for (Record r : records) {
            // Assuming reps are at even indices (0, 2, 4...)
            for (int i = 0; i < r.repsAndWeights.size(); i += 2) {
                allReps.add(r.repsAndWeights.get(i));
            }
        }

        if (allReps.isEmpty()) {
            throw new IllegalArgumentException("Keine Wiederholungen gefunden.");
        }

        double totalReps = 0;
        for (int r : allReps) {
            totalReps += r;
        }
        double avgReps = totalReps / allReps.size();

        double nextWeight;
        if (avgReps > 8) {
            nextWeight = currentWeight * 1.05;
        } else if (avgReps < 5) {
            nextWeight = currentWeight * 0.95;
        } else {
            nextWeight = currentWeight;
        }
        return Math.round(nextWeight * 10.0) / 10.0; // Round to one decimal place
    }

    /**
     * Main method for testing the FitnessAppGUI independently.
     * This allows you to run the FitnessAppGUI directly without going through the LoginWindow.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // For testing purposes, you can pass a dummy username
        SwingUtilities.invokeLater(() -> new FitnessAppGUI("testuser")); // Changed constructor call
    }
}
