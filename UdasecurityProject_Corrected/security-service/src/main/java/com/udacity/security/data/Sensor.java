package data;

public class Sensor {
    private String name;
    private com.udacity.security.data.SensorType sensorType;
    private Boolean active;

    public Sensor(String name, com.udacity.security.data.SensorType sensorType) {
        this.name = name;
        this.sensorType = sensorType;
        this.active = false;
    }


    public String getName() {
        return name;
    }

    public com.udacity.security.data.SensorType getSensorType() {
        return sensorType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}