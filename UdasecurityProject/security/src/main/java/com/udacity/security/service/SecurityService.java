package com.udacity.security.service;

import java.util.Set;
import java.util.HashSet;

import com.udacity.security.data.*;
import com.udacity.imageservice.service.ImageService;

public class SecurityService {
    private final SecurityRepository securityRepository;
    private final ImageService imageService;

    public SecurityService(SecurityRepository securityRepository, ImageService imageService) {
        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }

    public void changeSensorActivationStatus(Sensor sensor, boolean active) {
        if (sensor.isActive() == active) return; // No change if already in desired state
        sensor.setActive(active);
        securityRepository.updateSensor(sensor);

        ArmingStatus armingStatus = securityRepository.getArmingStatus();
        AlarmStatus alarmStatus = securityRepository.getAlarmStatus();

        if (armingStatus != ArmingStatus.DISARMED) {
            if (active) {
                if (alarmStatus == AlarmStatus.PENDING_ALARM) {
                    securityRepository.setAlarmStatus(AlarmStatus.ALARM);
                } else if (alarmStatus == AlarmStatus.NO_ALARM) {
                    securityRepository.setAlarmStatus(AlarmStatus.PENDING_ALARM);
                }
            } else if (alarmStatus == AlarmStatus.PENDING_ALARM && allSensorsInactive()) {
                securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
            }
        }
    }

    public void setArmingStatus(ArmingStatus armingStatus) {
        securityRepository.setArmingStatus(armingStatus);
        if (armingStatus == ArmingStatus.DISARMED) {
            securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
        } else {
            resetAllSensors();
            if (armingStatus == ArmingStatus.ARMED_HOME && imageService.imageContainsCat(null)) {
                securityRepository.setAlarmStatus(AlarmStatus.ALARM);
            }
        }
    }

    public void processImage(byte[] imageData) {
        if (securityRepository.getArmingStatus() == ArmingStatus.ARMED_HOME && imageService.imageContainsCat(imageData)) {
            securityRepository.setAlarmStatus(AlarmStatus.ALARM);
        } else if (!imageService.imageContainsCat(imageData) && allSensorsInactive()) {
            securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
        }
    }

    private boolean allSensorsInactive() {
        return securityRepository.getSensors().stream().noneMatch(Sensor::isActive);
    }

    private void resetAllSensors() {
        securityRepository.getSensors().forEach(sensor -> {
            sensor.setActive(false);
            securityRepository.updateSensor(sensor);
        });
    }

    public AlarmStatus getAlarmStatus() {
        return securityRepository.getAlarmStatus();
    }

    public ArmingStatus getArmingStatus() {
        return securityRepository.getArmingStatus();
    }

    public Set<Sensor> getSensors() {
        return securityRepository.getSensors();
    }

    public void addSensor(Sensor sensor) {
        securityRepository.addSensor(sensor);
    }

    public void removeSensor(Sensor sensor) {
        securityRepository.removeSensor(sensor);
    }
}