package com.udacity.imageservice.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;

import java.nio.ByteBuffer;
import java.util.List;

public class AwsImageService implements ImageService {
    private final AmazonRekognition rekognitionClient;

    public AwsImageService() {
        this.rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
    }

    @Override
    public boolean imageContainsCat(byte[] imageData) {
        if (imageData == null) return false;

        ByteBuffer imageBytes = ByteBuffer.wrap(imageData);
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withBytes(imageBytes))
                .withMaxLabels(10)
                .withMinConfidence(75F);

        List<Label> labels = rekognitionClient.detectLabels(request).getLabels();
        return labels.stream().anyMatch(label -> "Cat".equalsIgnoreCase(label.getName()));
    }
}