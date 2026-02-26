package org.example.parking.models.enums;

public enum CarType {
    CAR("Легковой"),
    TRUCK("Грузовой"),
    MOTORCYCLE("Мотоцикл"),
    BUS("Автобус");

    private final String displayName;
    CarType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
