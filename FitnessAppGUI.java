import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class FitnessAppGUI extends JFrame  {
    private JPanel mainPanel;
    private ArrayList<ExercisePanel> exercises ;
    private ArrayList<String> exerciseNames = new ArrayList<>(); // Liste der verfügbaren Übungsnamen
    private String username = "Manuel";
    private static final String BASE_DIR = "users";

    public FitnessAppGUI() {
        setTitle("Fitness App");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        exercises = new ArrayList<>();

        // Menüleiste oben rechts mit "Übungen"-Button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton addExerciseNameButton = new JButton("Übungen");
        addExerciseNameButton.addActionListener(e -> openExerciseNameEditor());
        topPanel.add(addExerciseNameButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton addExerciseButton = new JButton("Übung +");
        addExerciseButton.addActionListener(e -> promptForExercise());
        add(addExerciseButton, BorderLayout.SOUTH);

        loadExerciseNames(); // Übungen beim Start laden
        FitnessApp.createUser("Manuel"); // Benutzer anlegen, falls nicht vorhanden
        setVisible(true);
    }

    private void promptForExercise() {
        if (exerciseNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keine Übungen vorhanden. Bitte zuerst über 'Übungen' hinzufügen.");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Wähle eine Übung:",
            "Übung hinzufügen",
            JOptionPane.PLAIN_MESSAGE,
            null,
            exerciseNames.toArray(),
            exerciseNames.get(0)
        );

        if (selected != null && !selected.trim().isEmpty()) {
            addExercise(selected);
        }
    }

    private void openExerciseNameEditor() {
        JTextField newNameField = new JTextField(15);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String name : exerciseNames) {
            listModel.addElement(name);
        }

        JList<String> nameList = new JList<>(listModel);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addButton = new JButton("Hinzufügen");
        addButton.addActionListener(e -> {
            
            String newName = newNameField.getText().trim();
            if (!newName.isEmpty() && !exerciseNames.contains(newName)) {
                exerciseNames.add(newName);
                FitnessApp.createExercise(username , newName); // Benutzername "test" für Demo
                listModel.addElement(newName);
                newNameField.setText("");
                saveExerciseNames(); // Änderungen speichern
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Neuer Name:"));
        inputPanel.add(newNameField);
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(nameList), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Übungen bearbeiten", JOptionPane.PLAIN_MESSAGE);
    }

    private void addExercise(String title) {
        ExercisePanel newExercise = new ExercisePanel(title);
        exercises.add(newExercise);
        mainPanel.add(newExercise);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    

    private class ExercisePanel extends JPanel {
    private JPanel setsPanel;
    private ArrayList<RowPanel> sets;
    private String title;
    private boolean abgeschlossen = false;

    public ExercisePanel(String title) {
        this.title = title;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));

        sets = new ArrayList<>();
        setsPanel = new JPanel();
        setsPanel.setLayout(new BoxLayout(setsPanel, BoxLayout.Y_AXIS));

        JScrollPane setScrollPane = new JScrollPane(setsPanel);
        setScrollPane.setPreferredSize(new Dimension(550, 150));

        JButton addSetButton = new JButton("Set +");
        addSetButton.addActionListener(e -> {
            if (!abgeschlossen) addSet();
        });

        JButton finishButton = new JButton("Übung abschließen");
        finishButton.addActionListener(e -> abschliessen());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addSetButton);
        buttonPanel.add(finishButton);

        add(setScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addSet(); // Erste Set-Zeile hinzufügen
    }

    private void addSet() {
        RowPanel newRow = new RowPanel();
        sets.add(newRow);
        setsPanel.add(newRow);
        setsPanel.revalidate();
        setsPanel.repaint();
    }

    private void abschliessen() {
        if (abgeschlossen) return;

        ArrayList<String> daten = new ArrayList<>();
        daten.add(title);
        daten.add("done");

        for (RowPanel row : sets) {
            String gewicht = row.getWeight();
            String wdh = row.getReps();
            daten.add(gewicht);
            daten.add(wdh);
            row.setEditable(false);
        }

        abgeschlossen = true;
        List<Integer> RepsandWeights = new ArrayList<>();
        String exName = daten.get(0); // Übungsname ist das erste Element
        // Ab Index 2: abwechselnd Gewicht und Wiederholungen als Integer speichern
        for (int i = 2; i < daten.size(); i += 2) {
            try {
                int gewicht = Integer.parseInt(daten.get(i));
                int wdh = Integer.parseInt(daten.get(i + 1));
                RepsandWeights.add(wdh);      // Erst Wiederholungen
                RepsandWeights.add(gewicht);  // dann Gewicht
            } catch (Exception e) {
                // Fehlerhafte Eingaben überspringen
            }
        }
        FitnessApp.addRecord(username, exName , getdatum(), RepsandWeights); // an andere Klasse senden
    }
}


    private class RowPanel extends JPanel {
    private JTextField weightField;
    private JTextField repsField;
    private JLabel infoLabel;

    public RowPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setMaximumSize(new Dimension(500, 40));
        setPreferredSize(new Dimension(500, 40));

        infoLabel = new JLabel("Info:");
        weightField = new JTextField(5);
        repsField = new JTextField(5);

        add(infoLabel);
        add(new JLabel("Gewicht:"));
        add(weightField);
        add(new JLabel("Wdh.:"));
        add(repsField);
    }

    public void setLabelText(String text) {
        infoLabel.setText(text);
    }

    public String getWeight() {
        return weightField.getText().trim();
    }

    public String getReps() {
        return repsField.getText().trim();
    }

    public void setEditable(boolean editable) {
        weightField.setEditable(editable);
        repsField.setEditable(editable);
    }
    }

    public static String getdatum() {
        String heute = java.time.LocalDate.now().toString();
        return heute;
    }

    // Speichert die Übungsnamen dauerhaft in users/<username>/exercises.txt
    private void saveExerciseNames() {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get(BASE_DIR, username);
            java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path file = dir.resolve("exercises.txt");
            java.nio.file.Files.write(file, exerciseNames);
        } catch (Exception e) {
            // Fehler ignorieren oder loggen
        }
    }

    // Lädt die Übungsnamen aus users/<username>/exercises.txt in exerciseNames
    private void loadExerciseNames() {
        exerciseNames.clear();
        java.nio.file.Path file = java.nio.file.Paths.get(BASE_DIR, username, "exercises.txt");
        if (java.nio.file.Files.exists(file)) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(file);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !exerciseNames.contains(trimmed)) {
                        exerciseNames.add(trimmed);
                    }
                }
            } catch (Exception e) {
                // Fehler ignorieren oder loggen
            }
        }
    }

    
    public static void main(String[] args) {
        new FitnessAppGUI();
    }

}
