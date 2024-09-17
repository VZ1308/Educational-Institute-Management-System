package com.example.classes;

public class Teilnehmer extends com.example.classes.Person {
    private int teilnehmerId; // Wird von der Datenbank automatisch gesetzt

    public Teilnehmer(String name, String adresse, String geburtstag, String email, String telefon) {
        super(name, adresse, geburtstag, email, telefon);
    }

    @Override
    public String getTableName() {
        return "teilnehmer";
    }

    @Override
    public String getInsertSQL() {
        return String.format(
                "INSERT INTO teilnehmer (name, adresse, geburtstag, email, telefon) VALUES ('%s', '%s', '%s', '%s', '%s')",
                getName(), getAdresse(), getGeburtsdatum(), getEmail(), getTelefon()
        );
    }

    @Override
    public void anzeigen() {
        super.anzeigen();
        System.out.println("ID: " + getTeilnehmerId());
        System.out.println("Name: " + getName());
        System.out.println("Adresse: " + getAdresse());
        System.out.println("Geburtstag: " + getGeburtsdatum());
        System.out.println("Email: " + getEmail());
        System.out.println("Telefon: " + getTelefon());
    }


    public void setTeilnehmerId(int teilnehmerId) {
        this.teilnehmerId = teilnehmerId;
    }

    public int getTeilnehmerId() {
        return teilnehmerId;
    }
}
