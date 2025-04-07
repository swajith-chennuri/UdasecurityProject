package data;

import com.udacity.security.data.AlarmStatus;

import java.util.Set;

public interface SecurityRepository {
    void addSensor(com.udacity.security.data.Sensor sensor);
    void removeSensor(com.udacity.security.data.Sensor sensor);
    void updateSensor(com.udacity.security.data.Sensor sensor);

    void addSensor(Sensor sensor);

    void removeSensor(Sensor sensor);

    void updateSensor(Sensor sensor);

    void setAlarmStatus(AlarmStatus alarmStatus);

    void setArmingStatus(com.udacity.security.data.ArmingStatus armingStatus);

    void setArmingStatus(ArmingStatus armingStatus);

    void setCatDetected(boolean catDetected);
    Set<Sensor> getSensors();
    com.udacity.security.data.AlarmStatus getAlarmStatus();
    com.udacity.security.data.ArmingStatus getArmingStatus();
    boolean getCatDetected();

    void setAlarmStatus(AlarmStatus alarmStatus);
}