package com.udacity.security.application;

import com.udacity.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.security.data.SecurityRepository;
import com.udacity.imageservice.service.FakeImageService;
import com.udacity.imageservice.service.ImageService;
import com.udacity.security.service.SecurityService;

public class SecurityApplication {
    private final SecurityRepository securityRepository;
    private final ImageService imageService;
    private final SecurityService securityService;

    public SecurityApplication() {
        this.securityRepository = new PretendDatabaseSecurityRepositoryImpl();
        this.imageService = new FakeImageService();
        this.securityService = new SecurityService(securityRepository, imageService);
        System.out.println("Security Application Started");
    }

    public static void main(String[] args) {
        new SecurityApplication();
    }
}