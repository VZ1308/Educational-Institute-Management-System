import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuHandling {

    public static void startMenu(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Bildungsinstituts-Verwaltung");
            System.out.println("1. Mitarbeiter hinzufügen");
            System.out.println("2. Mitarbeiter anzeigen");
            System.out.println("3. Teilnehmer hinzufügen");
            System.out.println("4. Teilnehmer anzeigen");
            System.out.println("5. Mitarbeiter einem Kurs zuweisen");
            System.out.println("6. Teilnehmer einem Kurs zuweisen");
            System.out.println("7. Kurs hinzufügen");
            System.out.println("8. Kurse anzeigen");
            System.out.println("9. Zurück zum Hauptmenü");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Konsolenpuffer leeren

            switch (choice) {
                case 1 -> hinzufuegen(conn, scanner, "mitarbeiter");
                case 2 -> anzeigen(conn, "mitarbeiter");
                case 3 -> hinzufuegen(conn, scanner, "teilnehmer");
                case 4 -> anzeigen(conn, "teilnehmer");
                case 5 -> zuweisen(conn, scanner, "mitarbeiter");
                case 6 -> zuweisen(conn, scanner, "teilnehmer");
                case 7 -> kursHinzufuegen(conn, scanner);
                case 8 -> anzeigen(conn, "kurs");
                case 9 -> {
                    System.out.println("---Hauptmenü---");
                    return;
                }
                default -> System.out.println("Ungültige Auswahl.");
            }
        }
    }

    private static void hinzufuegen(Connection conn, Scanner scanner, String typ) {
        System.out.printf("%s hinzufügen:%n", capitalize(typ));

        Map<String, String> daten = new HashMap<>();
        daten.put("name", eingabe(scanner, "Name"));
        daten.put("adresse", eingabe(scanner, "Adresse"));
        daten.put("geburtstag", Validator.validiereDatum(scanner)); // Aufruf der Validator-Methode
        daten.put("email", Validator.validiereEmail(scanner));
        daten.put("telefon", Validator.validiereTelefon(scanner));
        String sql = switch (typ) {
            case "mitarbeiter" -> {
                daten.put("position", eingabe(scanner, "Position"));
                yield "INSERT INTO mitarbeiter (name, adresse, geburtstag, telefon, position, email) VALUES (?, ?, ?, ?, ?, ?)";
            }
            case "teilnehmer" -> "INSERT INTO teilnehmer (name, adresse, geburtstag, email, telefon) VALUES (?, ?, ?, ?, ?)";
            default -> throw new IllegalArgumentException("Unbekannter Typ: " + typ);
        };

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, daten.get("name"));
            stmt.setString(2, daten.get("adresse"));
            stmt.setString(3, daten.get("geburtstag"));
            stmt.setString(4, daten.get("telefon"));

            if (typ.equals("mitarbeiter")) {
                stmt.setString(5, daten.get("position"));
                stmt.setString(6, daten.get("email"));
            } else {
                stmt.setString(5, daten.get("email"));
            }

            stmt.executeUpdate();
            System.out.printf("%s wurde erfolgreich hinzugefügt.%n", capitalize(typ));
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private static String eingabe(Scanner scanner, String feld) {
        System.out.print(feld + ": ");
        return scanner.nextLine();
    }

    private static void anzeigen(Connection conn, String typ) {
        // SQL-Statement basierend auf dem Typ (mitarbeiter, teilnehmer oder kurs)
        String sql = switch (typ) {
            case "mitarbeiter" -> "SELECT * FROM mitarbeiter";
            case "teilnehmer" -> "SELECT * FROM teilnehmer";
            case "kurs" -> "SELECT * FROM kurs";
            default -> throw new IllegalArgumentException("Unbekannter Typ: " + typ);
        };

        // Versuche die Daten aus der Datenbank abzurufen und anzuzeigen
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Überschrift anzeigen basierend auf dem Typ
            System.out.printf("%s-Liste:%n", capitalize(typ));

            while (rs.next()) {
                Map<String, String> daten = new LinkedHashMap<>();

                // Je nach Typ die entsprechenden Spalten abfragen
                if (typ.equals("mitarbeiter")) {
                    daten.put("ID", String.valueOf(rs.getInt("mitarbeiterId")));
                    daten.put("Name", rs.getString("name"));
                    daten.put("Adresse", rs.getString("adresse"));
                    daten.put("Geburtsdatum", rs.getString("geburtstag"));
                    daten.put("Telefon", rs.getString("telefon"));
                    daten.put("Email", rs.getString("email"));
                    daten.put("Position", rs.getString("position"));
                } else if (typ.equals("teilnehmer")) {
                    daten.put("ID", String.valueOf(rs.getInt("teilnehmerId")));
                    daten.put("Name", rs.getString("name"));
                    daten.put("Adresse", rs.getString("adresse"));
                    daten.put("Geburtsdatum", rs.getString("geburtstag"));
                    daten.put("Telefon", rs.getString("telefon"));
                    daten.put("Email", rs.getString("email"));
                } else if (typ.equals("kurs")) {
                    daten.put("ID", String.valueOf(rs.getInt("kursId")));
                    daten.put("Kursname", rs.getString("kursname"));
                    daten.put("Beschreibung", rs.getString("beschreibung"));
                    daten.put("Startdatum", rs.getString("startdatum"));
                    daten.put("Enddatum", rs.getString("enddatum"));
                }

                // Daten anzeigen
                daten.forEach((k, v) -> System.out.printf("%s: %s%n", k, v));
                System.out.println("-------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }


    private static void zuweisen(Connection conn, Scanner scanner, String typ) {
        System.out.printf("%s einem Kurs zuweisen:%n", capitalize(typ));

        int id = Integer.parseInt(eingabe(scanner, String.format("%s ID", capitalize(typ))));
        int kursId = Integer.parseInt(eingabe(scanner, "Kurs-ID"));

        String sql = switch (typ) {
            case "mitarbeiter" -> "INSERT INTO mitarbeiter_kurs (kursId, mitarbeiterId) VALUES (?, ?)";
            case "teilnehmer" -> "INSERT INTO anmeldung (kursId, teilnehmerId) VALUES (?, ?)";
            default -> throw new IllegalArgumentException("Unbekannter Typ: " + typ);
        };

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kursId);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.printf("%s wurde dem Kurs erfolgreich zugewiesen.%n", capitalize(typ));
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    public static void kursHinzufuegen(Connection conn, Scanner scanner) {
        System.out.println("Kurs hinzufügen:");

        try {
            Map<String, String> kursdaten = new HashMap<>();
            kursdaten.put("kursname", eingabe(scanner, "Kursname"));
            kursdaten.put("beschreibung", eingabe(scanner, "Beschreibung"));
            kursdaten.put("startdatum", Validator.validiereDatum(scanner)); // Aufruf der Validator-Methode
            kursdaten.put("enddatum", Validator.validiereDatum(scanner)); // Aufruf der Validator-Methode

            String sql = "INSERT INTO kurs (kursname, beschreibung, startdatum, enddatum) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, kursdaten.get("kursname"));
                stmt.setString(2, kursdaten.get("beschreibung"));
                stmt.setDate(3, Date.valueOf(kursdaten.get("startdatum")));
                stmt.setDate(4, Date.valueOf(kursdaten.get("enddatum")));
                stmt.executeUpdate();
                System.out.println("Kurs wurde erfolgreich hinzugefügt.");
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
