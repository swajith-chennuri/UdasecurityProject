package service;

import com.udacity.imageservice.service.ImageService;
import com.udacity.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityServiceIntegrationTest {
    private com.udacity.security.service.SecurityService securityService;
    private com.udacity.security.data.FakeSecurityRepository securityRepository;
    private ImageService imageService;

    private com.udacity.security.data.Sensor sensor1, sensor2;

    @BeforeEach
    void init() {
        securityRepository = new com.udacity.security.data.FakeSecurityRepository();
        imageService = mock(ImageService.class);
        securityService = new com.udacity.security.service.SecurityService(securityRepository, imageService);

        sensor1 = new com.udacity.security.data.Sensor("Sensor1", com.udacity.security.data.SensorType.DOOR);
        sensor2 = new com.udacity.security.data.Sensor("Sensor2", com.udacity.security.data.SensorType.WINDOW);

        securityRepository.addSensor(sensor1);
        securityRepository.addSensor(sensor2);
    }

    @Test
    void systemArmed_resetsSensorsToInactive() {
        // Arrange
        sensor1.setActive(true);
        sensor2.setActive(true);
        securityRepository.updateSensor(sensor1);
        securityRepository.updateSensor(sensor2);

        // Act
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        // Assert
        assertFalse(securityRepository.getSensors().stream()
                .anyMatch(Sensor::getActive));
    }

    @Test
    void alarmTriggered_whenCatDetectedAndArmedHome() {
        // Arrange
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        // Act
        securityService.processImage(mock(BufferedImage.class));

        // Assert
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    @Test
    void alarmNotTriggered_whenCatDetectedAndDisarmed() {
        // Arrange
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        // Act
        securityService.processImage(mock(BufferedImage.class));

        // Assert
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    @Test
    void alarmStateChanges_whenSensorsActivatedDeactivated() {
        // Arrange
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        // Act & Assert - First sensor activates (should go to PENDING)
        sensor1.setActive(true);
        securityService.processSensorActivation(sensor1);
        assertEquals(AlarmStatus.PENDING, securityService.getAlarmStatus());

        // Second sensor activates (should go to ALARM)
        sensor2.setActive(true);
        securityService.processSensorActivation(sensor2);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());

        // First sensor deactivates (should stay ALARM)
        sensor1.setActive(false);
        securityService.processSensorActivation(sensor1);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());

        // Second sensor deactivates (should go to NO_ALARM)
        sensor2.setActive(false);
        securityService.processSensorActivation(sensor2);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
}