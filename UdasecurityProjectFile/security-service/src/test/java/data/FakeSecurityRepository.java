package service;

import com.udacity.security.data.AlarmStatus;
import data.SecurityRepository;
import data.Sensor;

import java.util.HashSet;
import java.util.Set;

public class FakeSecurityRepository implements SecurityRepository {
    private Set<Sensor> sensors = new HashSet<>();
    private com.udacity.security.data.AlarmStatus alarmStatus = com.udacity.security.data.AlarmStatus.NO_ALARM;
    private com.udacity.security.data.ArmingStatus armingStatus = com.udacity.security.data.ArmingStatus.DISARMED;
    private boolean catDetected = false;

    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
    }

    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
    }

    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {

    }

    public void setAlarmStatus() {
        setAlarmStatus((com.udacity.security.data.AlarmStatus)null);
    }

    @Override
    public void setAlarmStatus(com.udacity.security.data.AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    @Override
    public void setArmingStatus(com.udacity.security.data.ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
    }

    public void addSensor() {
        addSensor((com.udacity.security.data.Sensor) null);
    }

    @Override
    public void addSensor(com.udacity.security.data.Sensor sensor) {

    }

    @Override
    public void removeSensor(com.udacity.security.data.Sensor sensor) {

    }

    @Override
    public void updateSensor(com.udacity.security.data.Sensor sensor) {

    }

    @Override
    public void setArmingStatus(com.udacity.security.data.ArmingStatus armingStatus) {

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

    @Override
    public void setAlarmStatus(com.udacity.security.data.AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}