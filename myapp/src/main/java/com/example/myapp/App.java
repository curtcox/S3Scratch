package com.example.myapp;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;


public class App {

    static Region region = Region.US_WEST_2;
    final S3Client s3;
    final String key = "key";
    final String tempBucket = "bucket" + System.currentTimeMillis();


    App(S3Client s3) {
        this.s3 = s3;
    }

    void doStuff() {
        listBuckets();
        createBucket();
        waitUntilBucketExists();
        uploadObject();
        listBuckets();

        println("Cleaning up...");
        deleteObject();
        deleteBucket();
        println("Cleanup complete");
        println("");
        listBuckets();

        close();
        println("Exiting...");
    }

    void listBuckets() {
        println("Buckets Info =");
        for (Bucket bucket : s3.listBuckets().buckets()) {
            dump(bucket);
        }
    }

    void dump(Bucket bucket) {
        println(bucket.creationDate() + " " + bucket.name());
    }

    void close() {
        println("Closing the connection to Amazon S3");
        s3.close();
        println("Connection closed");
    }

    void uploadObject() {
        println("Uploading object...");

        s3.putObject(PutObjectRequest.builder().bucket(tempBucket).key(key)
                        .build(),
                RequestBody.fromString("Testing with the AWS SDK for Java"));

        println("Upload complete");
        println("");
    }

    void createBucket() {
        s3.createBucket(CreateBucketRequest
                .builder()
                .bucket(tempBucket)
                .createBucketConfiguration(
                        CreateBucketConfiguration.builder()
                                .locationConstraint(region.id())
                                .build())
                .build());
        println("Creating bucket: " + tempBucket);
    }

    void waitUntilBucketExists() {
        s3.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                .bucket(tempBucket)
                .build());
        println(tempBucket +" is ready.");
    }

    void deleteObject() {
        println("Deleting object: " + key);
        s3.deleteObject(DeleteObjectRequest.builder().bucket(tempBucket).key(key).build());
        println(key +" has been deleted.");
    }

    void deleteBucket() {
        println("Deleting bucket: " + tempBucket);
        s3.deleteBucket(DeleteBucketRequest.builder().bucket(tempBucket).build());
        println(tempBucket +" has been deleted.");
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
