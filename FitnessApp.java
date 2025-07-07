import java.io.*;
import java.util.*;

public class FitnessApp{


    private static final String BASE_DIR = "users";

    

    // Neue Übung anlegen
    public static boolean createExercise(String username, String exName) {
        File userDir = new File(BASE_DIR, username);
        File exFile = new File(userDir, exName + ".txt");
        if (exFile.exists()) {
            return false;
        } else {
            try {
                return exFile.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
    }

    // Alle Übungen eines Benutzers auflisten
    public static List<String> listExercises(String username) {
        File userDir = new File(BASE_DIR, username);
        String[] exercises = userDir.list((dir, name) -> name.endsWith(".txt"));
        if (exercises == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String ex : exercises) {
            result.add(ex.replace(".txt", ""));
        }
        return result;
    }
// test
    // Datensatz hinzufügen
    public static boolean addRecord(String username, String exName, String date, List<Integer> repsAndWeights) {
        File exFile = new File(BASE_DIR + "/" + username, exName + ".txt");
        try (FileWriter fw = new FileWriter(exFile, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append(date);
            for (int val : repsAndWeights) sb.append(",").append(val);
            fw.write(sb.toString() + System.lineSeparator());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Datensätze auslesen
    public static List<Record> getRecords(String username, String exName) {
        File exFile = new File(BASE_DIR + "/" + username, exName + ".txt");
        List<Record> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(exFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    List<Integer> repsAndWeights = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        repsAndWeights.add(Integer.parseInt(parts[i]));
                    }
                    records.add(new Record(parts[0], repsAndWeights));
                }
            }
        } catch (IOException e) {
            // Fehler ignorieren, leere Liste zurückgeben
        }
        return records;
    }  

    // Datensatz-Klasse
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
     * @param exName   Name der Übung
     * @return Gewichtsvorschlag (auch wenn unverändert)
     */
    public static double calculateNextWeight(String username, String exName) {
        List<Record> records = getRecords(username, exName);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("Keine Datensätze vorhanden.");
        }
        // Wir nehmen den ersten Satz aller Einträge als Beispiel (kann angepasst werden)
        List<Integer> reps = new ArrayList<>();
        for (Record r : records) {
            if (!r.repsAndWeights.isEmpty()) {
                reps.add(r.repsAndWeights.get(0)); // Nur die Wiederholungen des 1. Satzes
            }
        }
        if (reps.isEmpty()) {
            throw new IllegalArgumentException("Keine Wiederholungen gefunden.");
        }
        // Das aktuelle Gewicht des letzten Eintrags, 2. Wert im letzten Record (1. Satz)
        int currentWeight = 0;
        List<Integer> last = records.get(records.size() - 1).repsAndWeights;
        if (last.size() > 1) {
            currentWeight = last.get(1);
        } else {
            throw new IllegalArgumentException("Kein Gewicht im letzten Datensatz gefunden.");
        }
        int total = 0;
        for (int r : reps) {
            total += r;
        }
        double avg = (double) total / reps.size();
        double nextWeight;
        if (avg > 8) {
            nextWeight = currentWeight * 1.05;
        } else if (avg < 5) {
            nextWeight = currentWeight * 0.95;
        } else {
            nextWeight = currentWeight;
        }
        return Math.round(nextWeight * 10.0) / 10.0;
    }

}
