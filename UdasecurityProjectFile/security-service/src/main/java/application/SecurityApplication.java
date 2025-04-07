package application;

import com.udacity.imageservice.service.FakeImageService;
import com.udacity.security.data.ArmingStatus;
import com.udacity.security.data.SecurityRepository;
import com.udacity.security.service.SecurityService;

import java.io.IOException;

public class SecurityApplication {
    public static void main(String[] args) throws IOException {
        SecurityRepository securityRepository = new SecurityRepository() {
            @Override
            public ArmingStatus getArmingStatus() {
                return null;
            }
        };

        FakeImageService imageService = new FakeImageService();
        SecurityService securityService = new SecurityService(securityRepository, imageService);

        // GUI would be initialized here in a real application
        System.out.println("Security Application Started");
    }
}
