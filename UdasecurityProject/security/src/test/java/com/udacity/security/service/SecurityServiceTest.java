package com.udacity.security.service;

import com.udacity.security.data.*;
import com.udacity.imageservice.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class SecurityServiceTest {
    private SecurityService securityService;

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityService = new SecurityService(securityRepository, imageService);
    }

    @Test
    void sensorActivated_alarmArmed_systemPending() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    void sensorActivated_pendingAlarm_systemAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void allSensorsInactive_pendingAlarm_noAlarm() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void sensorChange_alarmActive_noChange() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorActivated_alreadyActive_pendingToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void sensorDeactivated_alreadyInactive_noChange() {
        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void catDetected_armedHome_systemAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(null)).thenReturn(true);
        securityService.processImage(null);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void noCat_sensorsInactive_noAlarm() {
        when(imageService.imageContainsCat(null)).thenReturn(false);
        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        securityService.processImage(null);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void systemDisarmed_noAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void systemArmed_resetSensors() {
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(new Sensor("Test1", SensorType.DOOR));
        sensors.add(new Sensor("Test2", SensorType.WINDOW));
        when(securityRepository.getSensors()).thenReturn(sensors);
        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);
        for (Sensor sensor : sensors) {
            verify(securityRepository).updateSensor(argThat(s -> !s.isActive()));
        }
    }

    @Test
    void catDetected_armedHomeFromDisarmed_systemAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(imageService.imageContainsCat(null)).thenReturn(true);
        securityService.processImage(null); // Cat detected but not armed yet
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
}