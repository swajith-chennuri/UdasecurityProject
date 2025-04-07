package com.udacity.imageservice.service;

import java.awt.image.BufferedImage;

public class FakeImageService implements ImageService {
    @Override
    public boolean imageContainsCat(BufferedImage image, float confidenceThresh) {
        // Stubbed response for testing
        return false;
    }
}
