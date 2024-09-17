package com.example.classes;

public abstract class Person {
    private String name;
    private String adresse;
    private String geburtstag;
    private String email;
    private String telefon;

    public Person(String name, String adresse, String geburtstag, String email, String telefon) {
        setName(name);
        setAdresse(adresse);
        setGeburtsdatum(geburtstag);
        setEmail(email);
        setTelefon(telefon);
    }

    public abstract String getTableName();

    public abstract String getInsertSQL();

    public void anzeigen() {
        System.out.println("Name: " + name);
        System.out.println("Adresse: " + adresse);
        System.out.println("Geburtsdatum: " + geburtstag);
        System.out.println("Email: " + email);
        System.out.println("Telefon: " + telefon);
    }

    // Getter und Setter mit Validierung
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer oder null sein.");
        }
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        if (adresse == null || adresse.isEmpty()) {
            throw new IllegalArgumentException("Adresse darf nicht leer oder null sein.");
        }
        this.adresse = adresse;
    }

    public String getGeburtsdatum() {
        return geburtstag;
    }

    public void setGeburtsdatum(String geburtstag) {
        if (geburtstag == null || geburtstag.isEmpty()) {
            throw new IllegalArgumentException("Geburtsdatum darf nicht leer sein.");
        }
        this.geburtstag = geburtstag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Ungültige E-Mail.");
        }
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        if (telefon == null || !telefon.matches("\\d+")) {
            throw new IllegalArgumentException("Telefonnummer darf nur Zahlen enthalten.");
        }
        this.telefon = telefon;
    }
}
