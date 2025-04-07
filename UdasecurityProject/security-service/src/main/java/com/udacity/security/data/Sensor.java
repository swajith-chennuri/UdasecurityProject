package com.udacity.security.data;

public class Sensor {
    private String name;
    private SensorType type;
    private boolean active;

    public Sensor(String name, SensorType type) {
        this.name = name;
        this.type = type;
        this.active = false;
    }

    public String getName() {
        return name;
    }

    public SensorType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + "(" + type + "): " + (active ? "Active" : "Inactive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return name.equals(sensor.name) && type == sensor.type;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }
}