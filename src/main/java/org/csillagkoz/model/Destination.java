package org.csillagkoz.model;

/**
 * Célállomás entitás
 */
public class Destination {
    private String name;
    private String type;       // "PLANET" vagy "MOON"
    private int basePrice;
    private int kedvezmenyesPrice;

    // Konstruktor (ezzel hozzuk létre az új célállomásokat)
    public Destination(String name, String type, int basePrice, int kedvezmenyesPrice) {
        this.name = name;
        this.type = type;
        this.basePrice = basePrice;
        this.kedvezmenyesPrice=kedvezmenyesPrice;
    }

    public int getKedvezmenyesPrice() {
        return kedvezmenyesPrice;
    }

    public void setKedvezmenyesPrice(int kedvezmenyesPrice) {
        this.kedvezmenyesPrice = kedvezmenyesPrice;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return name + " (" + (type.equalsIgnoreCase("MOON") ? "Hold" : "Bolygó") + ")";
    }
}