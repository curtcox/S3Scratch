package com.example.myapp;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;


public class App {

    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        String bucket = "bucket" + System.currentTimeMillis();
        String key = "key";

        tutorialSetup(s3, bucket, region);

        println("Uploading object...");

        s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                        .build(),
                RequestBody.fromString("Testing with the AWS SDK for Java"));

        println("Upload complete");
        println("");

        cleanUp(s3, bucket, key);

        println("Closing the connection to Amazon S3");
        s3.close();
        println("Connection closed");
        println("Exiting...");
    }

    public static void tutorialSetup(S3Client s3Client, String bucketName, Region region) {
        s3Client.createBucket(CreateBucketRequest
                .builder()
                .bucket(bucketName)
                .createBucketConfiguration(
                        CreateBucketConfiguration.builder()
                                .locationConstraint(region.id())
                                .build())
                .build());
        println("Creating bucket: " + bucketName);
        s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                .bucket(bucketName)
                .build());
        println(bucketName +" is ready.");
    }

    public static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
        println("Cleaning up...");
        println("Deleting object: " + keyName);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
        s3Client.deleteObject(deleteObjectRequest);
        println(keyName +" has been deleted.");
        println("Deleting bucket: " + bucketName);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3Client.deleteBucket(deleteBucketRequest);
        println(bucketName +" has been deleted.");
        println("");
        println("Cleanup complete");
        println("");
    }

    static void println(Object o) {
        System.out.println(o);
    }
}
