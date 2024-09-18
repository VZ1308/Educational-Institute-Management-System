import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class GUI {
    // Datenbankverbindung
    static Connection conn = DatabaseConnector.connect();

    // Regex zur Validierung von E-Mail-Adressen und Telefonnummern
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,4}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

    // Startpunkt der Anwendung
    public static void main(String[] args) {
        // Überprüfen, ob die Verbindung zur Datenbank erfolgreich ist
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Datenbankverbindung konnte nicht hergestellt werden. Das Programm wird beendet.");
            System.exit(1);
        }

        // Hauptfenster erstellen
        JFrame frame = new JFrame("Bildungsinstituts-Verwaltung");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Buttons für die verschiedenen Kategorien
        JButton employeesButton = new JButton("Mitarbeiter");
        JButton participantsButton = new JButton("Teilnehmer");
        JButton coursesButton = new JButton("Kurs");

        // Button-Größe festlegen
        Dimension buttonSize = new Dimension(200, 50);
        employeesButton.setPreferredSize(buttonSize);
        participantsButton.setPreferredSize(buttonSize);
        coursesButton.setPreferredSize(buttonSize);

        // Position der Buttons im Layout festlegen
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(employeesButton, gbc);

        gbc.gridy = 1;
        frame.add(participantsButton, gbc);

        gbc.gridy = 2;
        frame.add(coursesButton, gbc);

        // ActionListener für die Buttons
        employeesButton.addActionListener(e -> showEmployeesDialog(frame));
        participantsButton.addActionListener(e -> showParticipantsDialog(frame));
        coursesButton.addActionListener(e -> showCoursesDialog(frame));

        // Fenster sichtbar machen
        frame.setVisible(true);
    }

    // Dialogfenster für Mitarbeiter-Optionen anzeigen
    private static void showEmployeesDialog(JFrame parent) {
        JDialog employeeDialog = new JDialog(parent, "Mitarbeiter", true);
        employeeDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Blockiert das Hauptfenster

        employeeDialog.setSize(300, 200);
        employeeDialog.setLayout(new GridLayout(3, 1, 10, 10));

        JButton addEmployeeButton = new JButton("Mitarbeiter hinzufügen");
        JButton viewEmployeeButton = new JButton("Mitarbeiter anzeigen");
        JButton assignEmployeeButton = new JButton("Mitarbeiter einem Kurs zuweisen");

        addEmployeeButton.addActionListener(e -> showAddPersonDialog("Mitarbeiter"));
        viewEmployeeButton.addActionListener(e -> showViewDialog("mitarbeiter"));
        assignEmployeeButton.addActionListener(e -> showAssignDialog("Mitarbeiter"));

        employeeDialog.add(addEmployeeButton);
        employeeDialog.add(viewEmployeeButton);
        employeeDialog.add(assignEmployeeButton);

        employeeDialog.setVisible(true);
    }

    // Dialogfenster für Teilnehmer-Optionen anzeigen
    private static void showParticipantsDialog(JFrame parent) {
        JDialog participantDialog = new JDialog(parent, "Teilnehmer", true);
        participantDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Blockiert das Hauptfenster

        participantDialog.setSize(300, 200);
        participantDialog.setLayout(new GridLayout(3, 1, 10, 10));

        JButton addParticipantButton = new JButton("Teilnehmer hinzufügen");
        JButton viewParticipantButton = new JButton("Teilnehmer anzeigen");
        JButton assignParticipantButton = new JButton("Teilnehmer einem Kurs zuweisen");

        addParticipantButton.addActionListener(e -> showAddPersonDialog("Teilnehmer"));
        viewParticipantButton.addActionListener(e -> showViewDialog("teilnehmer"));
        assignParticipantButton.addActionListener(e -> showAssignDialog("Teilnehmer"));

        participantDialog.add(addParticipantButton);
        participantDialog.add(viewParticipantButton);
        participantDialog.add(assignParticipantButton);

        participantDialog.setVisible(true);
    }

    // Dialogfenster für Kurs-Optionen anzeigen
    private static void showCoursesDialog(JFrame parent) {
        JDialog courseDialog = new JDialog(parent, "Kurse", true);
        courseDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Blockiert das Hauptfenster

        courseDialog.setSize(300, 250);
        courseDialog.setLayout(new GridLayout(4, 1, 10, 10));

        JButton addCourseButton = new JButton("Kurs hinzufügen");
        JButton viewCourseButton = new JButton("Kurs anzeigen");
        JButton assignCourseButton = new JButton("Teilnehmer einem Kurs zuweisen");

        addCourseButton.addActionListener(e -> showAddCourseDialog());
        viewCourseButton.addActionListener(e -> showViewDialog("kurs"));
        assignCourseButton.addActionListener(e -> showAssignDialog("Teilnehmer"));

        courseDialog.add(addCourseButton);
        courseDialog.add(viewCourseButton);
        courseDialog.add(assignCourseButton);

        courseDialog.setVisible(true);
    }

    // Zeigt ein Dialogfenster zum Hinzufügen eines Mitarbeiters oder Teilnehmers an
    private static void showAddPersonDialog(String type) {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField positionField = type.equals("Mitarbeiter") ? new JTextField() : null;

        Object[] fields = {
                "Name:", nameField,
                "Adresse:", addressField,
                "Geburtsdatum (YYYY-MM-DD):", birthdayField,
                "Email:", emailField,
                "Telefon:", phoneField,
                type.equals("Mitarbeiter") ? "Position:" : null, positionField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, type + " hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (type.equals("Mitarbeiter")) {
                addEmployee(nameField.getText(), addressField.getText(), birthdayField.getText(), emailField.getText(), phoneField.getText(), positionField.getText());
            } else {
                addParticipant(nameField.getText(), addressField.getText(), birthdayField.getText(), emailField.getText(), phoneField.getText());
            }
        }
    }

    // Zeigt ein Dialogfenster mit einer Tabelle zur Anzeige von Mitarbeitern, Teilnehmern oder Kursen
    private static void showViewDialog(String type) {
        String sql = switch (type) {
            case "mitarbeiter" -> "SELECT * FROM mitarbeiter";
            case "teilnehmer" -> "SELECT * FROM teilnehmer";
            case "kurs" -> "SELECT * FROM kurs";
            default -> throw new IllegalArgumentException("Unbekannter Typ: " + type);
        };
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // Tabelle für die Daten erstellen
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);

            // Spaltenbreite anpassen
            TableColumnModel columnModel = table.getColumnModel();
            for (int i = 0; i < columnCount; i++) {
                columnModel.getColumn(i).setPreferredWidth(150); // Setze die Breite jeder Spalte auf 150 Pixel
            }

            // Scrollpane für die Tabelle erstellen und im Dialog anzeigen
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(null, scrollPane, type + " anzeigen", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Zeigt ein Dialogfenster zum Hinzufügen eines Kurses an
    private static void showAddCourseDialog() {
        JTextField courseNameField = new JTextField();
        JTextField courseDescriptionField = new JTextField();
        JTextField courseStartDateField = new JTextField();
        JTextField courseEndDateField = new JTextField();
        JTextField maxParticipantsField = new JTextField();

        Object[] fields = {
                "Kursname:", courseNameField,
                "Beschreibung:", courseDescriptionField,
                "Startdatum (YYYY-MM-DD):", courseStartDateField,
                "Enddatum (YYYY-MM-DD):", courseEndDateField,
                "Maximale Teilnehmer:", maxParticipantsField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Kurs hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            addCourse(courseNameField.getText(), courseDescriptionField.getText(), courseStartDateField.getText(), courseEndDateField.getText(), maxParticipantsField.getText());
        }
    }

    // Fügt einen neuen Mitarbeiter zur Datenbank hinzu
    private static void addEmployee(String name, String address, String birthday, String email, String phone, String position) {
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(null, "Ungültige E-Mail-Adresse.");
            return;
        }
        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(null, "Ungültige Telefonnummer.");
            return;
        }
        if (!isValidDate(birthday)) {
            JOptionPane.showMessageDialog(null, "Ungültiges Geburtsdatum.");
            return;
        }
        String sql = "INSERT INTO mitarbeiter (name, adresse, geburtsdatum, email, telefon, position) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setDate(3, Date.valueOf(birthday));
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, position);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Mitarbeiter erfolgreich hinzugefügt.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fügt einen neuen Teilnehmer zur Datenbank hinzu
    private static void addParticipant(String name, String address, String birthday, String email, String phone) {
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(null, "Ungültige E-Mail-Adresse.");
            return;
        }
        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(null, "Ungültige Telefonnummer.");
            return;
        }
        if (!isValidDate(birthday)) {
            JOptionPane.showMessageDialog(null, "Ungültiges Geburtsdatum.");
            return;
        }
        String sql = "INSERT INTO teilnehmer (name, adresse, geburtsdatum, email, telefon) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setDate(3, Date.valueOf(birthday));
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Teilnehmer erfolgreich hinzugefügt.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fügt einen neuen Kurs zur Datenbank hinzu
    private static void addCourse(String name, String description, String startDate, String endDate, String maxParticipants) {
        if (!isValidDate(startDate)) {
            JOptionPane.showMessageDialog(null, "Ungültiges Startdatum.");
            return;
        }
        if (!isValidDate(endDate)) {
            JOptionPane.showMessageDialog(null, "Ungültiges Enddatum.");
            return;
        }

        String sql = "INSERT INTO kurs (name, beschreibung, startdatum, enddatum, max_teilnehmer) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setInt(5, Integer.parseInt(maxParticipants));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Kurs erfolgreich hinzugefügt.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Zeigt ein Dialogfenster zur Zuweisung eines Mitarbeiters oder Teilnehmers zu einem Kurs an
    private static void showAssignDialog(String type) {
        JTextField idField = new JTextField();
        JTextField courseIdField = new JTextField();

        Object[] fields = {
                type + "-ID:", idField,
                "Kurs-ID:", courseIdField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, type + " einem Kurs zuweisen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            assignToCourse(type, idField.getText(), courseIdField.getText());
        }
    }

    // Zuweisung eines Mitarbeiters oder Teilnehmers zu einem Kurs
    private static void assignToCourse(String type, String id, String courseId) {
        String table = type.equals("Mitarbeiter") ? "mitarbeiter_kurs" : "anmeldung";
        String sql = "INSERT INTO " + table + " (" + type.toLowerCase() + "_id, kurs_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.setInt(2, Integer.parseInt(courseId));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, type + " erfolgreich dem Kurs zugewiesen.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validierung einer E-Mail-Adresse
    private static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    // Validierung einer Telefonnummer
    private static boolean isValidPhone(String phone) {
        return Pattern.compile(PHONE_REGEX).matcher(phone).matches();
    }

    // Validierung eines Datums
    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
