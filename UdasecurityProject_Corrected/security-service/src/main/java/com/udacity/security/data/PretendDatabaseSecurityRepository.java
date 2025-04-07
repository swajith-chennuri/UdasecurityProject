package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.SecurityRepository;
import data.Sensor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public abstract class PretendDatabaseSecurityRepository implements SecurityRepository {
    private final Gson gson = new Gson();
    private final Path filePath;
    private Set<Sensor> sensors;
    private com.udacity.security.data.AlarmStatus alarmStatus;
    private com.udacity.security.data.ArmingStatus armingStatus;
    private boolean catDetected;

    public PretendDatabaseSecurityRepository() throws IOException {
        this.filePath = Path.of(System.getProperty("user.home"), ".security", "sensors.json");
        initializeRepository();
    }

    private void initializeRepository() throws IOException {
        File file = filePath.toFile();
        if (file.exists()) {
            String json = Files.readString(filePath);
            Type type = new TypeToken<Set<Sensor>>() {}.getType();
            sensors = gson.fromJson(json, type);
        } else {
            sensors = new HashSet<>();
        }
        alarmStatus = com.udacity.security.data.AlarmStatus.NO_ALARM;
        armingStatus = com.udacity.security.data.ArmingStatus.DISARMED;
        catDetected = false;
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        saveToFile();
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
        saveToFile();
    }

    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
        saveToFile();
    }

    @Override
    public void setAlarmStatus(com.udacity.security.data.AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    @Override
    public void setArmingStatus(com.udacity.security.data.ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
    }

    @Override
    public void setCatDetected(boolean catDetected) {
        this.catDetected = catDetected;
    }

    @Override
    public Set<Sensor> getSensors() {
        return sensors;
    }

    @Override
    public com.udacity.security.data.AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public com.udacity.security.data.ArmingStatus getArmingStatus() {
        return armingStatus;
    }

    @Override
    public boolean getCatDetected() {
        return catDetected;
    }

    private void saveToFile() {
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, gson.toJson(sensors));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}