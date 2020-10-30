package fr.umlv.localkube;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import fr.umlv.localkube.utils.OperatingSystem;
import jdk.jshell.spi.ExecutionControl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class DockerManager {
    private static final String jarDirectoryName = "apps";
    private static final String dockerImagesDirectoryName = "docker-images";
    private final OperatingSystem os;


    public DockerManager(OperatingSystem os) {
        this.os = os;
    }

    public void createContainer(String jarFilename, String imageName) throws IOException, InvalidImageReferenceException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
        Jib.from("openjdk:15")
            .addLayer(Arrays.asList(getPathToJarFile(jarFilename)), AbsoluteUnixPath.get("/"))
            .setEntrypoint("java", "--enable-preview", "-jar", jarFilename)
            .containerize(Containerizer.to(TarImage.at(getPathToDockerImage(imageName)).named(imageName)));
    }

    public boolean checkIfDockerImageExists(String imageName) {
        return Files.exists(getPathToDockerImage(imageName));
    }

    public boolean checkIfJarFileExists(String jarFilename) {
        return Files.exists(getPathToJarFile(jarFilename));
    }

    public void loadImage() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void runImage() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Path getPathToJarFile(String jarFilename) {
        return Paths.get(String.join(os.getSeparator(), ".", jarDirectoryName, jarFilename));
        //return Paths.get(String.format(".%s%s%s%s", os.getSeparator(), jarDirectoryName, os.getSeparator(), jarFilename));
    }

    private Path getPathToDockerImage(String imageName) {
        return Paths.get(String.join(os.getSeparator(), ".", dockerImagesDirectoryName, imageName));
        //return Paths.get(String.format(".%s%s%s%s", os.getSeparator(), dockerImagesDirectoryName, os.getSeparator(), imageName));
    }
}
