package com.custom.securitysys.core;

import com.udacity.imageservice.service.ImageService;
import data.SecurityRepository;
import data.Sensor;

import java.awt.image.BufferedImage;
import java.util.Set;

public class HomeSecurityManager {
    private final SecurityRepository securityRepository;
    private final ImageService imageService;

    public HomeSecurityManager(SecurityRepository securityRepository, ImageService imageService) {
        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }

    public void setAlarmStatus(com.udacity.security.data.AlarmStatus alarmStatus) {
        securityRepository.setAlarmStatus(alarmStatus);
    }

    public void setArmingStatus(com.udacity.security.data.ArmingStatus armingStatus) {
        if (armingStatus == com.udacity.security.data.ArmingStatus.DISARMED) {
            setAlarmStatus(com.udacity.security.data.AlarmStatus.NO_ALARM);
        } else {
            // When armed, reset all sensors to inactive
            Set<Sensor> sensors = securityRepository.getSensors();
            sensors.forEach(sensor -> {
                sensor.setActive(false);
                securityRepository.updateSensor(sensor);
            });

            // If armed-home and cat detected, immediately alarm
            if (armingStatus == com.udacity.security.data.ArmingStatus.ARMED_HOME &&
                    securityRepository.getCatDetected()) {
                setAlarmStatus(com.udacity.security.data.AlarmStatus.ALARM);
            }
        }
        securityRepository.setArmingStatus(armingStatus);
    }

    public void processSensorActivation(Sensor sensor) {
        com.udacity.security.data.AlarmStatus alarmStatus = securityRepository.getAlarmStatus();
        com.udacity.security.data.ArmingStatus armingStatus = securityRepository.getArmingStatus();

        if (armingStatus == com.udacity.security.data.ArmingStatus.DISARMED) {
            return; // No changes if system is disarmed
        }

        if (alarmStatus == com.udacity.security.data.AlarmStatus.ALARM) {
            return; // No changes if alarm is already active
        }

        if (sensor.getActive()) {
            if (alarmStatus == com.udacity.security.data.AlarmStatus.PENDING) {
                setAlarmStatus(com.udacity.security.data.AlarmStatus.ALARM);
            } else {
                setAlarmStatus(com.udacity.security.data.AlarmStatus.PENDING);
            }
        } else {
            // Sensor deactivated
            boolean allSensorsInactive = securityRepository.getSensors().stream()
                    .noneMatch(Sensor::getActive);
            if (allSensorsInactive) {
                setAlarmStatus(com.udacity.security.data.AlarmStatus.NO_ALARM);
            }
        }
    }

    public void processImage(BufferedImage image) {
        if (image == null) return;

        boolean felineIdentified = imageService.imageContainsCat(image, 50.0f);
        securityRepository.setCatDetected(felineIdentified);

        if (felineIdentified && securityRepository.getArmingStatus() == com.udacity.security.data.ArmingStatus.ARMED_HOME) {
            setAlarmStatus(com.udacity.security.data.AlarmStatus.ALARM);
        } else if (!felineIdentified && securityRepository.getAlarmStatus() == com.udacity.security.data.AlarmStatus.ALARM) {
            boolean anySensorActive = securityRepository.getSensors().stream()
                    .anyMatch(Sensor::getActive);
            if (!anySensorActive) {
                setAlarmStatus(com.udacity.security.data.AlarmStatus.NO_ALARM);
            }
        }
    }

    // Getters for current alarmStatus
    public com.udacity.security.data.AlarmStatus getAlarmStatus() {
        return securityRepository.getAlarmStatus();
    }

    public com.udacity.security.data.ArmingStatus getArmingStatus() {
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