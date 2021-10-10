package com.cloudphoto.subcommand;

import com.cloudphoto.dto.AmazonS3Dto;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

//list
//list -a test
@Command(name = "list", description = "Get all albums", mixinStandardHelpOptions = true)
public final class List implements Runnable {

    @Option(names = {"-a", "--album"}, description = "List photo which linked this album", defaultValue = "")
    private String album;

    @Override
    public void run() {
        var s3 = new AmazonS3Dto();
        System.out.println("List...\n");
        if (album.isBlank()) {
            var result = s3.list();
            result.forEach(System.out::println);
        } else {
            try {
                var result = s3.list(album);
                if(!result.isEmpty()) {
                    result.forEach(System.out::println);
                } else System.out.println("Album is empty");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Album not found");
            }
        }
    }
}
