package service;

import com.udacity.imageservice.service.ImageService;
import com.udacity.security.data.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    private com.udacity.security.service.SecurityService securityService;
    @Mock private com.udacity.security.data.SecurityRepository securityRepository;
    @Mock private ImageService imageService;

    private Sensor sensor1;
    private Sensor sensor2;
    private Set<Sensor> sensors;

    @BeforeEach
    void init() {
        securityService = new SecurityService(securityRepository, imageService);

        sensor1 = new Sensor("Sensor1", SensorType.DOOR);
        sensor2 = new Sensor("Sensor2", SensorType.WINDOW);
        sensors = new HashSet<>();
        sensors.add(sensor1);
        sensors.add(sensor2);

        when(securityRepository.getSensors()).thenReturn(sensors);
    }

    @Test
    void alarmArmedAndSensorActivated_setPendingAlarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(com.udacity.security.data.ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(com.udacity.security.data.AlarmStatus.NO_ALARM);

        sensor1.setActive(true);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository).setAlarmStatus(com.udacity.security.data.AlarmStatus.PENDING);
    }

    @Test
    void alarmArmedAndSensorActivatedAndPending_setAlarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(com.udacity.security.data.ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING);

        sensor1.setActive(true);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void pendingAlarmAndAllSensorsInactive_returnNoAlarmState() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING);

        sensor1.setActive(false);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void alarmActiveAndSensorDeactivated_noChangeToAlarmState() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        sensor1.setActive(false);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorActivatedWhileAlreadyActiveAndPending_setAlarmState() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING);

        sensor1.setActive(true);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void sensorDeactivatedWhileAlreadyInactive_noChangeToAlarmState() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING);

        sensor1.setActive(false);
        securityService.processSensorActivation(sensor1);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void catDetectedAndSystemArmedHome_setAlarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository).setCatDetected(true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void catNotDetectedAndSensorsInactive_setNoAlarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(com.udacity.security.data.ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(com.udacity.security.data.AlarmStatus.ALARM);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository).setCatDetected(false);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void systemDisarmed_setNoAlarmStatus() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
        verify(securityRepository).setArmingStatus(ArmingStatus.DISARMED);
    }

    @Test
    void systemArmed_resetAllSensorsToInactive() {
        sensor1.setActive(true);
        sensor2.setActive(true);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        assertFalse(sensor1.getActive());
        assertFalse(sensor2.getActive());
        verify(securityRepository).updateSensor(sensor1);
        verify(securityRepository).updateSensor(sensor2);
    }

    @Test
    void systemArmedHomeAndCatDetected_setAlarmStatus() {
        when(securityRepository.getCatDetected()).thenReturn(true);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void systemArmed_resetSensorsInactive(ArmingStatus armingStatus) {
        sensor1.setActive(true);
        sensor2.setActive(true);

        securityService.setArmingStatus(armingStatus);

        assertFalse(sensor1.getActive());
        assertFalse(sensor2.getActive());
        verify(securityRepository).updateSensor(sensor1);
        verify(securityRepository).updateSensor(sensor2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void processImage_catDetectionSetsCorrectStatus(boolean catDetected) {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(catDetected);
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository).setCatDetected(catDetected);
        if (catDetected) {
            verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
        }
    }
}