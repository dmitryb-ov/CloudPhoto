package com.cloudphoto.subcommand;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.cloudphoto.dto.AmazonS3Dto;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

//download -p C:\Users\dima2\Downloads\cloud -a test
@Command(name = "download", description = "Download poto", mixinStandardHelpOptions = true)
public final class Download implements Runnable {

    @CommandLine.Option(names = {"-p", "--path"}, description = "path", required = true)
    private String path;

    @CommandLine.Option(names = {"-a", "--album"}, description = "album", required = true)
    private String album;

    @Override
    public void run() {
        if (!path.isBlank() && !album.isBlank()) {
            var paths = Paths.get(path);

            try {
                if (Files.exists(paths)) {
                    System.out.println("Download...\n");
                    download();
                } else System.err.println("Path is not exists");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else System.err.println("Path or album is empty");
    }

    private void download() throws IOException {
        InputStream obj = null;
        try {
            var s3 = new AmazonS3Dto();
            var result = s3.download(album);

            if (!result.isEmpty()) {

                for (S3Object s3Object : result) {
                    obj = s3Object.getObjectContent();
                    Files.copy(obj, Paths.get(path + "/" + s3Object.getKey().split("/")[1]));
                }
                System.out.println("Done!");
            } else System.out.println(album + " is empty");

        } catch (FileAlreadyExistsException e) {
            System.err.println("File already exist");

        } catch (AmazonS3Exception e) {
            System.err.println("Album not found");

        } finally {
            if (obj != null) {
                obj.close();
            }
        }
    }
}
