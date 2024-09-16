import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Verbindung zur Datenbank herstellen
        Connection conn = DatabaseConnector.connect();
        Scanner scanner = new Scanner(System.in);

        if (conn != null) {
            try {
                while (true) {
                    System.out.println("Guten Tag! Möchten Sie mit der \n1) Gui oder mit der \n2) Command Line arbeiten? \n3) oder das Programm beenden  \nBitte geben Sie Ihre Auswahl (1 - 3) ein:");

                    int choice = getValidIntegerInput(scanner);

                    if (choice == 1) {
                        System.out.println("GUI wird gestartet...");
                        GUI.main(null); // GUI wird gestartet
                        break;


                    } else if (choice == 2) {
                        // Menü starten
                        MenuHandling.startMenu(conn);
                    }
                    else if(choice == 3) {
                        return;
                    }else {
                        System.out.println("Bitte geben Sie eine gültige Zahl zwischen 1 und 3 ein.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Ein Fehler ist aufgetreten: " + e.getMessage());
                e.printStackTrace(); // Detaillierte Fehlerausgabe
            } finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close(); // Verbindung schließen
                    }
                } catch (SQLException ex) {
                    System.out.println("Fehler beim Schließen der Datenbankverbindung: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            System.out.println("Verbindung zur Datenbank konnte nicht hergestellt werden.");
        }

        scanner.close(); // Scanner schließen, um Ressourcenlecks zu vermeiden
    }

    private static int getValidIntegerInput(Scanner scanner) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input == 1 || input == 2 || input == 3) {
                    return input;
                } else {
                    System.out.println("Bitte geben Sie eine gültige Zahl (1 - 3) ein:");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein:");
                scanner.next(); // Eingabepuffer leeren, um falsche Eingaben zu verwerfen
            }
        }
    }
}
