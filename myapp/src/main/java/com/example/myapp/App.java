package com.example.myapp;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;


public class App {

    static Region region = Region.US_WEST_2;
    final S3Client s3;
    final String key = "key";
    final String bucket = "bucket" + System.currentTimeMillis();


    App(S3Client s3) {
        this.s3 = s3;
    }

    void doStuff() {
        createBucket();
        waitUntilBucketExists();
        uploadObject();

        println("Cleaning up...");
        deleteObject();
        deleteBucket();
        println("Cleanup complete");
        println("");

        close();
        println("Exiting...");
    }

    void close() {
        println("Closing the connection to Amazon S3");
        s3.close();
        println("Connection closed");
    }

    void uploadObject() {
        println("Uploading object...");

        s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                        .build(),
                RequestBody.fromString("Testing with the AWS SDK for Java"));

        println("Upload complete");
        println("");
    }

    void createBucket() {
        s3.createBucket(CreateBucketRequest
                .builder()
                .bucket(bucket)
                .createBucketConfiguration(
                        CreateBucketConfiguration.builder()
                                .locationConstraint(region.id())
                                .build())
                .build());
        println("Creating bucket: " + bucket);
    }

    void waitUntilBucketExists() {
        s3.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                .bucket(bucket)
                .build());
        println(bucket +" is ready.");
    }

    void deleteObject() {
        println("Deleting object: " + key);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        s3.deleteObject(deleteObjectRequest);
        println(key +" has been deleted.");
    }

    void deleteBucket() {
        println("Deleting bucket: " + bucket);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
        s3.deleteBucket(deleteBucketRequest);
        println(bucket +" has been deleted.");
        println("");
    }

    static void println(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) {
        S3Client s3 = S3Client.builder().region(region).build();
        App app = new App(s3);
        app.doStuff();
    }

}
