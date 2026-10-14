package org.csillagkoz.model;

/**
 * Utas entitás
 */
public class Passenger {
    private String name;              // Utas neve
    private String galacticId;        // Galaktikus azonosító / Útlevélszám
    private int age;                  // Életkor
    private boolean healthCertificate;// Egészségügyi igazolás státusza (igen/nem)

    public Passenger(String name, String galacticId, int age, boolean healthCertificate) {
        this.name = name;
        this.galacticId = galacticId;
        this.age = age;
        this.healthCertificate = healthCertificate;
    }

    // Getters és Setters
    public String getName() {
        return name;
    }

    public String getGalacticId() {
        return galacticId;
    }

    public int getAge() {
        return age;
    }

    public boolean isHealthCertificate() {
        return healthCertificate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGalacticId(String galacticId) {
        this.galacticId = galacticId;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHealthCertificate(boolean healthCertificate) {
        this.healthCertificate = healthCertificate;
    }

    @Override
    public String toString() {
        return name + " (ID: " + galacticId + ", Kor: " + age + ")";
    }
}