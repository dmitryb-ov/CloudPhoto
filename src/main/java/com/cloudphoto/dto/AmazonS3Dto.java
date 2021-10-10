package com.cloudphoto.dto;

import com.amazonaws.services.s3.model.S3Object;
import com.cloudphoto.s3.AmazonS3Configuration;
import com.cloudphoto.s3.CrudS3;

import java.io.File;
import java.util.List;
import java.util.Set;

public class AmazonS3Dto implements CrudS3 {
    private static CrudS3 s3;

    public AmazonS3Dto() {
        s3 = new AmazonS3Configuration();
    }

    @Override
    public void upload(File file, String album) {
        s3.upload(file, album);
    }

    @Override
    public List<S3Object> download(String album) {
        return s3.download(album);
    }

    @Override
    public Set<String> list() {
        return s3.list();
    }

    @Override
    public List<String> list(String album) throws ArrayIndexOutOfBoundsException {
        return s3.list(album);
    }
}
