package com.app.merrbioapi.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.rekognition.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.rekognition.secretKey:}")
    private String secretKey;

    @Value("${aws.rekognition.region:us-east-1}")
    private String region;

    @Bean
    @ConditionalOnProperty(name = "app.image-moderation.enabled", havingValue = "true")
    public AmazonRekognition amazonRekognition() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
        return AmazonRekognitionClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}