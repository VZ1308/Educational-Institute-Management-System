import javax.swing.*; // importiert Klassen aus der Swing-Bibliothek für die GUI
import java.awt.*; // importiert Klassen für Layouts
import java.awt.event.ActionEvent; // importiert Klassen für Ereignisse, z.B. Button-Klicks
import java.awt.event.ActionListener; // importiert Schnittstelle für die Verarbeitung von Ereignissen
import java.sql.*; // importiert Klassen für die Arbeit mit der Datenbank
import java.time.LocalDate; // importiert Klasse für die Arbeit mit Datumswerten ohne Zeitangaben
import java.time.format.DateTimeParseException; // importiert Klasse für das Parsen und Verarbeiten von Datumsformaten
import java.util.regex.Pattern; // importiert Klasse für die Regex-Verarbeitung

public class GUI {
    // Deklariert eine statische Variable, um die Datenbankverbindung zu speichern
    static Connection conn = null;

    // Regex für die Validierung von E-Mail-Adressen
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    // Regex für die Validierung von Telefonnummern
    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";

    public static void main(String[] args) {
        // Verbindung zur Datenbank herstellen
        conn = DatabaseConnector.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Datenbankverbindung konnte nicht hergestellt werden. Das Programm wird beendet.");
            System.exit(1);
        }

        JFrame frame = new JFrame("Bildungsinstituts-Verwaltung");
        // Stellt sicher, dass das Fenster beim Beenden geschlossen wird
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Erstellen des Panels
        JPanel panel = new JPanel(); // Erstellt ein Panel als Container
        panel.setLayout(new GridLayout(8, 1)); // Setzt das Layout des Panels

        // Buttons erstellen
        JButton addEmployeeButton = new JButton("Mitarbeiter hinzufügen");
        JButton viewEmployeeButton = new JButton("Mitarbeiter anzeigen");
        JButton addParticipantButton = new JButton("Teilnehmer hinzufügen");
        JButton viewParticipantButton = new JButton("Teilnehmer anzeigen");
        JButton assignEmployeeButton = new JButton("Mitarbeiter einem Kurs zuweisen");
        JButton assignParticipantButton = new JButton("Teilnehmer einem Kurs zuweisen");
        JButton addCourseButton = new JButton("Kurs hinzufügen");
        JButton exitButton = new JButton("Programm beenden");

        // Buttons zum Panel hinzufügen
        panel.add(addEmployeeButton);
        panel.add(viewEmployeeButton);
        panel.add(addParticipantButton);
        panel.add(viewParticipantButton);
        panel.add(assignEmployeeButton);
        panel.add(assignParticipantButton);
        panel.add(addCourseButton);
        panel.add(exitButton);

        // Panel zum Frame in der Mitte des Layouts hinzufügen
        frame.add(panel, BorderLayout.CENTER);

        // ActionListener für die Buttons, der auf Klicks wartet
        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        viewEmployeeButton.addActionListener(e -> showViewDialog("mitarbeiter"));
        addParticipantButton.addActionListener(e -> showAddParticipantDialog());
        viewParticipantButton.addActionListener(e -> showViewDialog("teilnehmer"));
        assignEmployeeButton.addActionListener(e -> showAssignDialog("mitarbeiter"));
        assignParticipantButton.addActionListener(e -> showAssignDialog("teilnehmer"));
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    // Zeigt ein Dialogfenster zum Hinzufügen eines Mitarbeiters an
    private static void showAddEmployeeDialog() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField positionField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Adresse:", addressField,
                "Geburtsdatum (YYYY-MM-DD):", birthdayField,
                "Email:", emailField,
                "Telefon:", phoneField,
                "Position:", positionField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Mitarbeiter hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            addEmployee(nameField.getText(), addressField.getText(), birthdayField.getText(), emailField.getText(), phoneField.getText(), positionField.getText());
        }
    }

    // Zeigt ein Dialogfenster zum Hinzufügen eines Teilnehmers an
    private static void showAddParticipantDialog() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Adresse:", addressField,
                "Geburtsdatum (YYYY-MM-DD):", birthdayField,
                "Email:", emailField,
                "Telefon:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Teilnehmer hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            addParticipant(nameField.getText(), addressField.getText(), birthdayField.getText(), emailField.getText(), phoneField.getText());
        }
    }

    // Zeigt ein Dialogfenster zum Zuweisen eines Mitarbeiters oder Teilnehmers zu einem Kurs an
    private static void showAssignDialog(String type) {
        JTextField idField = new JTextField();
        JTextField courseIdField = new JTextField();

        Object[] fields = {
                type.equals("mitarbeiter") ? "Mitarbeiter ID:" : "Teilnehmer ID:", idField,
                "Kurs-ID:", courseIdField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, type + " einem Kurs zuweisen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                int courseId = Integer.parseInt(courseIdField.getText());
                assignToCourse(type, id, courseId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Bitte geben Sie gültige numerische Werte für ID und Kurs-ID ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Zeigt ein Dialogfenster zum Hinzufügen eines Kurses an
    private static void showAddCourseDialog() {
        JTextField courseNameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();

        Object[] fields = {
                "Kursname:", courseNameField,
                "Beschreibung:", descriptionField,
                "Startdatum (YYYY-MM-DD):", startDateField,
                "Enddatum (YYYY-MM-DD):", endDateField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Kurs hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            addCourse(courseNameField.getText(), descriptionField.getText(), startDateField.getText(), endDateField.getText());
        }
    }

    // Zeigt die Daten für Mitarbeiter oder Teilnehmer in einem Dialogfenster an
    private static void showViewDialog(String type) {
        StringBuilder sb = new StringBuilder();
        try {
            String sql = type.equals("mitarbeiter") ? "SELECT * FROM mitarbeiter" : "SELECT * FROM teilnehmer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt(type.equals("mitarbeiter") ? "mitarbeiterId" : "teilnehmerId")).append("\n");
                sb.append("Name: ").append(rs.getString("name")).append("\n");
                sb.append("Adresse: ").append(rs.getString("adresse")).append("\n");
                sb.append("Geburtsdatum: ").append(rs.getString("geburtstag")).append("\n");
                sb.append("Telefon: ").append(rs.getString("telefon")).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");

                if (type.equals("mitarbeiter")) {
                    sb.append("Position: ").append(rs.getString("position")).append("\n");
                }

                sb.append("-------------------------------\n");
            }
        } catch (SQLException e) {
            sb.append("Fehler: ").append(e.getMessage()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), type + " Liste", JOptionPane.INFORMATION_MESSAGE);
    }

    // Fügt einen Mitarbeiter zur Datenbank hinzu
    private static void addEmployee(String name, String address, String birthday, String email, String phone, String position) {
        if (name.isEmpty() || address.isEmpty() || birthday.isEmpty() || email.isEmpty() || phone.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige E-Mail-Adresse ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches(PHONE_REGEX, phone)) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Telefonnummer ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validierung des Datumsformats
        try {
            LocalDate.parse(birthday);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Parsen des Geburtsdatums. Bitte verwenden Sie das Format YYYY-MM-DD.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO mitarbeiter (name, adresse, geburtstag, telefon, position, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, birthday);
            stmt.setString(4, phone);
            stmt.setString(5, position);
            stmt.setString(6, email);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Mitarbeiter erfolgreich hinzugefügt.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fehler: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fügt einen Teilnehmer zur Datenbank hinzu
    private static void addParticipant(String name, String address, String birthday, String email, String phone) {
        if (name.isEmpty() || address.isEmpty() || birthday.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige E-Mail-Adresse ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches(PHONE_REGEX, phone)) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Telefonnummer ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validierung des Datumsformats
        try {
            LocalDate.parse(birthday);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Parsen des Geburtsdatums. Bitte verwenden Sie das Format YYYY-MM-DD.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO teilnehmer (name, adresse, geburtstag, email, telefon) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, birthday);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Teilnehmer erfolgreich hinzugefügt.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fehler: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fügt einen Kurs zur Datenbank hinzu
    private static void addCourse(String courseName, String description, String startDate, String endDate) {
        if (courseName.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validierung der Datumsformate
        try {
            LocalDate startDatum = LocalDate.parse(startDate);
            LocalDate endDatum = LocalDate.parse(endDate);
            if (startDatum.isAfter(endDatum)) {
                JOptionPane.showMessageDialog(null, "Das Startdatum darf nicht nach dem Enddatum liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO kurs (kursname, beschreibung, startdatum, enddatum) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseName);
                stmt.setString(2, description);
                stmt.setDate(3, java.sql.Date.valueOf(startDatum));
                stmt.setDate(4, java.sql.Date.valueOf(endDatum));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Kurs erfolgreich hinzugefügt.");
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Parsen der Daten: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Hinzufügen des Kurses: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Weist einen Mitarbeiter oder Teilnehmer einem Kurs zu
    private static void assignToCourse(String type, int id, int courseId) {
        if (id <= 0 || courseId <= 0) {
            JOptionPane.showMessageDialog(null, "ID und Kurs-ID müssen positive Zahlen sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = type.equals("mitarbeiter") ? "INSERT INTO mitarbeiter_kurs (kursId, mitarbeiterId) VALUES (?, ?)" : "INSERT INTO anmeldung (kursId, teilnehmerId) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, type + " wurde dem Kurs erfolgreich zugewiesen.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fehler: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
