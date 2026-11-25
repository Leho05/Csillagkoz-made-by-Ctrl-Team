package org.csillagkoz.model;

/**
 * Utas entitás
 */
public class Passenger {
    private String name;              // Utas neve
    private String galacticId;        // Galaktikus azonosító / Útlevélszám
    private int age;                  // Életkor

    public Passenger(String name, String galacticId, int age) {
        this.name = name;
        this.galacticId = galacticId;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getGalacticId() {
        return galacticId;
    }

    public int getAge() {
        return age;
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

    @Override
    public String toString() {
        return name + " (ID: " + galacticId + ", Kor: " + age + ")";
    }
}