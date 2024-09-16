import java.sql.Connection;           // Importiert die Connection-Klasse aus dem java.sql-Paket
import java.sql.DriverManager;        // Importiert die DriverManager-Klasse, um eine Verbindung zu erstellen
import java.sql.SQLException;         // Importiert die SQLException-Klasse, um Fehler bei der Datenbankverbindung zu behandeln

public class DatabaseConnector {     // Definiert eine öffentliche Klasse namens DatabaseConnection
    private static final String URL = "jdbc:mysql://localhost:3306/bildungsinstitut"; // URL zur Datenbank
    private static final String USER = "root";       // Benutzername für den Datenbankzugriff
    private static final String PASSWORD = "";       // Passwort für den Datenbankzugriff

    // Diese Methode stellt eine Verbindung zur Datenbank her und gibt ein Connection-Objekt zurück
    public static Connection connect() {
        Connection conn = null;       // Deklariert ein Connection-Objekt, das initial null ist
        try {
            // Versucht, eine Verbindung zur angegebenen URL mit dem Benutzernamen und Passwort herzustellen
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Verbindung zur Datenbank erfolgreich!");  // Gibt eine Erfolgsmeldung aus
        } catch (SQLException e) {    // Fängt eine SQLException, falls die Verbindung fehlschlägt
            System.out.println("Verbindung zur Datenbank fehlgeschlagen: " + e.getMessage()); // Gibt die Fehlermeldung aus
        }
        return conn;  // Gibt die Datenbankverbindung zurück (oder null, falls die Verbindung fehlschlug)
    }
}
