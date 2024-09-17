package com.example.classes;

public class Mitarbeiter extends com.example.classes.Person {
    private int mitarbeiterId;
    private String position;

    public Mitarbeiter(String name, String adresse, String geburtstag,  String telefon, String position, String email) {
        super(name, adresse, geburtstag, telefon, email);
        setPosition(position);
    }

    @Override
    public String getTableName() {
        return "mitarbeiter";
    }

    @Override
    public String getInsertSQL() {
        return String.format(
                "INSERT INTO mitarbeiter (name, adresse, geburtstag, telefon, position, email, ) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                getName(), getAdresse(), getGeburtsdatum(), getEmail(), getTelefon(), getPosition()
        ); //String.format() ermöglicht es uns, Zeichenfolgen mit Platzhaltern zu erstellen, die später mit Werten gefüllt werden
    }

    @Override
    public void anzeigen() {
        System.out.println("ID: " + getMitarbeiterId());
        System.out.println("Name: " + getName());
        System.out.println("Adresse: " + getAdresse());
        System.out.println("Geburtstag: " + getGeburtsdatum());
        System.out.println("Email: " + getEmail());
        System.out.println("Telefon: " + getTelefon());
        System.out.println("Position: " + getPosition());
    }

    // Getter und Setter mit Validierung

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (position == null || position.isEmpty()) {
            throw new IllegalArgumentException("Position darf nicht leer oder null sein.");
        }
        this.position = position;
    }

    public int getMitarbeiterId() {
        return mitarbeiterId;
    }

    public void setMitarbeiterId(int mitarbeiterId) {
        this.mitarbeiterId = mitarbeiterId;
    }
}
