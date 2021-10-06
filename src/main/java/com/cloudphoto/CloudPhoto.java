package com.cloudphoto;

import com.cloudphoto.subcommand.Download;
import com.cloudphoto.subcommand.List;
import com.cloudphoto.subcommand.Upload;
import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;

@Command(name = "cloudphoto", description = "This is cloudphoto app",
        mixinStandardHelpOptions = true,
        subcommands = {Upload.class, Download.class, List.class})
public class CloudPhoto implements Runnable {

    public static void main(String[] args) {
        PicocliRunner.run(CloudPhoto.class, args);
        System.exit(0);
    }

    @Override
    public void run() {
        //
    }
}
