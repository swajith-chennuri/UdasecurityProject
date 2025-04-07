package com.udacity.security.data;

import java.util.Set;

public interface SecurityRepository {
    void setAlarmStatus(AlarmStatus alarmStatus);
    AlarmStatus getAlarmStatus();
    void setArmingStatus(ArmingStatus armingStatus);
    ArmingStatus getArmingStatus();
    Set<Sensor> getSensors();
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
}