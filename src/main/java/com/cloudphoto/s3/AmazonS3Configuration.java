package com.cloudphoto.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmazonS3Configuration implements CrudS3 {
    private static final String ENDPOINT = "storage.yandexcloud.net";
    private static final String REGION = "ru-central1";
    private static final String BUCKET_NAME = "cloudphoto";

    private AmazonS3 s3;

    public AmazonS3Configuration() {
        this.s3 = createS3Context();
    }

    @Override
    public void upload(File file, String album) {
        s3.putObject(new PutObjectRequest(BUCKET_NAME, album + "/" + file.getName(), file));
    }

    @Override
    public List<S3Object> download(String album) {
        var request = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withPrefix(album);
        ListObjectsV2Result result;
        List<S3Object> objects = new ArrayList<>();

        do {
            result = s3.listObjectsV2(request);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                objects.add(s3.getObject(BUCKET_NAME, objectSummary.getKey()));
            }
        } while (result.isTruncated());

        return objects;
    }

    @Override
    public Set<String> list() {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(BUCKET_NAME);
        ListObjectsV2Result result;
        Set<String> folders = new HashSet<>();

        do {
            result = s3.listObjectsV2(req);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                var folderName = objectSummary.getKey().split("/")[0];
                folders.add(folderName);
            }

        } while (result.isTruncated());

        return folders;
    }

    @Override
    public List<String> list(String album) {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withPrefix(album);
        ListObjectsV2Result result;
        List<String> photoNames = new ArrayList<>();

        do {
            result = s3.listObjectsV2(req);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                photoNames.add(objectSummary.getKey().split("/")[1]);
            }
        } while (result.isTruncated());

        return photoNames;
    }

    private AmazonS3 createS3Context() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(ENDPOINT, REGION)
                )
                .build();
    }
}
