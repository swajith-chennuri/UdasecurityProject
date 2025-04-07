package com.udacity.imageservice.service;

public class FakeImageService implements ImageService {
    @Override
    public boolean imageContainsCat(byte[] imageData) {
        // Simulate cat detection (always true for simplicity)
        return true;
    }
}