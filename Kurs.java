package com.example.classes;

import java.sql.Date;
import java.time.LocalDate;

public class Kurs {
    private int kursId; // Wird von der Datenbank automatisch gesetzt
    private String kursname;
    private String beschreibung;
    private LocalDate startdatum;
    private LocalDate enddatum;

    // Konstruktor ohne kursId, da diese automatisch von der Datenbank vergeben wird
    public Kurs(String kursname, String beschreibung, LocalDate startdatum, LocalDate enddatum) {
        setName(kursname);
        setBeschreibung(beschreibung);
        setStartdatum(startdatum);
        setEnddatum(enddatum);
    }

    // Getter und Setter mit Validierung

    public String getName() {
        return kursname;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer oder null sein.");
        }
        this.kursname = name;
    }

    public void setBeschreibung(String beschreibung) {
        if (beschreibung == null || beschreibung.isEmpty()) {
            throw new IllegalArgumentException("Beschreibung darf nicht leer oder null sein.");
        }
        this.beschreibung = beschreibung;
    }

    public void setStartdatum(LocalDate startdatum) {
        if (startdatum == null) {
            throw new IllegalArgumentException("Startdatum darf nicht null sein.");
        }
        this.startdatum = startdatum;
    }

    public LocalDate getEnddatum() {
        return enddatum;
    }

    public void setEnddatum(LocalDate enddatum) {
        if (enddatum == null) {
            throw new IllegalArgumentException("Enddatum darf nicht null sein.");
        }
        if (enddatum.isBefore(startdatum)) {
            throw new IllegalArgumentException("Enddatum darf nicht vor dem Startdatum liegen.");
        }
        this.enddatum = enddatum;
    }

    // Methode zum Anzeigen der Kursinformationen
    public void anzeigen() {
        System.out.println("Kurs-ID: " + (kursId > 0 ? kursId : "noch nicht zugewiesen"));
        System.out.println("Kursname: " + kursname);
        System.out.println("Beschreibung: " + beschreibung);
        System.out.println("Startdatum: " + startdatum);
        System.out.println("Enddatum: " + enddatum);
    }

    // SQL-Befehl für das Einfügen eines neuen Kurses
    public String getInsertSQL() {
        return String.format(
                "INSERT INTO kurs (kursname, beschreibung, startdatum, enddatum) VALUES ('%s', '%s', '%s', '%s')",
                kursname, beschreibung, Date.valueOf(startdatum), Date.valueOf(enddatum)
        );
    }
}
