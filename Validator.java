import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Validator {

    // Validiert die Email-Adresse
    public static String validiereEmail(Scanner scanner) {
        String email;
        Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (emailPattern.matcher(email).matches()) {
                break;
            } else {
                System.out.println("Ungültige Email-Adresse. Bitte erneut eingeben.");
            }
        }
        return email;
    }

    // Validiert die Telefonnummer
    public static String validiereTelefon(Scanner scanner) {
        String telefon;
        Pattern telefonPattern = Pattern.compile("^\\+?[0-9-]{7,15}$");

        while (true) {
            System.out.print("Telefon: ");
            telefon = scanner.nextLine();
            if (telefonPattern.matcher(telefon).matches()) {
                break;
            } else {
                System.out.println("Ungültige Telefonnummer. Bitte erneut eingeben.");
            }
        }
        return telefon;
    }

    // Validiert das Datum und überprüft das Format (YYYY-MM-DD)
    public static String validiereDatum(Scanner scanner) {
        String datum;
        while (true) {
            System.out.print("Geburtsdatum (YYYY-MM-DD): ");
            datum = scanner.nextLine();
            try {
                LocalDate.parse(datum); // Versucht, das Datum zu parsen
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ungültiges Datum. Bitte das Format YYYY-MM-DD verwenden.");
            }
        }
        return datum;
    }
}
