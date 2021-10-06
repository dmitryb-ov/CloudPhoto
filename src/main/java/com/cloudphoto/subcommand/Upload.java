package com.cloudphoto.subcommand;

import com.cloudphoto.dto.AmazonS3Dto;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

//upload -a test -p C:\Users\dima2\Downloads\cloud
@Command(name = "upload", description = "Upload photo into ya cloud", mixinStandardHelpOptions = true)
public final class Upload implements Runnable {

    @Option(names = {"-p", "--path"}, description = "path", required = true)
    private String path;

    @Option(names = {"-a", "--album"}, description = "album", required = true)
    private String album;

    @Override
    public void run() {
        if (!path.isBlank() && !album.isBlank()) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                paths.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)
                            && (FilenameUtils.getExtension(filePath.toString()).equals("jpeg")
                            || FilenameUtils.getExtension(filePath.toString()).equals("jpg"))) {
                        var s3 = new AmazonS3Dto();
                        s3.upload(filePath.toFile(), album);
                        System.out.println(filePath);
                    }
                });
            } catch (NoSuchFileException e) {
                System.err.println(e.getMessage() + " not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Path or album is empty");
        }
    }
}
