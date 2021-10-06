package com.cloudphoto.s3;

import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface CrudS3 {

    void upload(File file, String album);

    List<S3Object> download(String album);

    Set<String> list();

    List<String> list(String album);
}
