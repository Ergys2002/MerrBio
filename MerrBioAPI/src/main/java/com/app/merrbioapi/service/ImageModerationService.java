package com.app.merrbioapi.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.app.merrbioapi.exception.ImageModerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Service for checking images for inappropriate content using AWS Rekognition
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageModerationService {

    private final AmazonRekognition rekognitionClient;

    @Value("${app.image-moderation.enabled:true}")
    private boolean moderationEnabled;

    @Value("${app.image-moderation.nudity-threshold:0.6}")
    private double nudityThreshold;

    /**
     * Checks if an image contains inappropriate content
     * @param file The image file to check
     * @return true if the image is safe, false if it contains inappropriate content
     * @throws ImageModerationException if the moderation check fails
     */
    public boolean isSafeImage(MultipartFile file) throws ImageModerationException {
        if (!moderationEnabled) {
            log.info("Image moderation is disabled. Skipping check.");
            return true;
        }

        if (file == null || file.isEmpty()) {
            return true;
        }

        try {
            byte[] bytes = file.getBytes();
            ByteBuffer imageBytes = ByteBuffer.wrap(bytes);

            // Create Rekognition detect moderation labels request
            DetectModerationLabelsRequest request = new DetectModerationLabelsRequest()
                    .withImage(new Image().withBytes(imageBytes))
                    .withMinConfidence(20F); // Set minimum confidence threshold

            // Call the API
            DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(request);
            List<ModerationLabel> labels = result.getModerationLabels();

            log.info("Moderation labels detected: {}", labels.size());

            // Check for explicit content
            boolean containsExplicitContent = checkForExplicitContent(labels);
            double maxNudityScore = getMaxNudityScore(labels);

            log.info("Image moderation result - maxNudityScore: {}, containsExplicitContent: {}",
                    maxNudityScore, containsExplicitContent);

            // Determine if the image is safe based on threshold
            return maxNudityScore < nudityThreshold && !containsExplicitContent;

        } catch (AmazonRekognitionException e) {
            log.error("Error from Rekognition service", e);
            throw new ImageModerationException("Failed to moderate image: " + e.getMessage());
        } catch (IOException e) {
            log.error("Error reading image file", e);
            throw new ImageModerationException("Failed to read image file: " + e.getMessage());
        }
    }

    /**
     * Check if any of the moderation labels indicate explicit content
     */
    private boolean checkForExplicitContent(List<ModerationLabel> labels) {
        for (ModerationLabel label : labels) {
            String name = label.getName().toLowerCase();

            // Check for explicit content categories
            if (name.contains("explicit nudity") ||
                    name.contains("sexual activity") ||
                    name.contains("graphic violence") ||
                    name.contains("drug use")) {

                // If confidence is high (over 70%), consider it explicit
                if (label.getConfidence() > 70.0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the maximum score for nudity-related labels
     */
    private double getMaxNudityScore(List<ModerationLabel> labels) {
        double maxScore = 0.0;

        for (ModerationLabel label : labels) {
            String name = label.getName().toLowerCase();
            if (name.contains("nudity") || name.contains("revealing clothes")) {
                double confidence = label.getConfidence() / 100.0; // Convert percentage to 0-1 scale
                maxScore = Math.max(maxScore, confidence);
            }
        }

        return maxScore;
    }
}