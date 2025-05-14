package com.scoutingtcg.purchases.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Service
public class S3ClientService {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Getter
    @Value("${aws.s3.buckets.receipts}")
    private String receiptsBucket;

    @Getter
    @Value("${aws.s3.buckets.pokemon}")
    private String pokemonBucket;

    @Getter
    @Value("${aws.s3.buckets.products}")
    private String productsBucket;

    @Getter
    @Value("${aws.s3.endpoints.receipts}")
    private String receiptsEndpoint;

    @Getter
    @Value("${aws.s3.endpoints.pokemon}")
    private String pokemonEndpoint;

    @Getter
    @Value("${aws.s3.endpoints.products}")
    private String productsEndpoint;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

    }

    public String uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            return resolveEndpoint(bucketName) + fileName;

        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Error al subir archivo a S3: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String bucketName, String urlImage) {
        String key = extractKeyFromUrl(urlImage);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Error al eliminar archivo de S3: " + e.getMessage(), e);
        }
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf(".com/") + 5);
    }

    private String resolveEndpoint(String bucketName) {
        if (bucketName.equals(receiptsBucket)) {
            return receiptsEndpoint;
        } else if (bucketName.equals(pokemonBucket)) {
            return pokemonEndpoint;
        } else if (bucketName.equals(productsBucket)) {
            return productsEndpoint;
        } else {
            throw new IllegalArgumentException("Bucket name not recognized: " + bucketName);
        }
    }

}
