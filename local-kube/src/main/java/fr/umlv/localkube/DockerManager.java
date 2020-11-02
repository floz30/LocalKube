package fr.umlv.localkube;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.utils.OperatingSystem;

import java.io.File;
import java.io.FileNotFoundException;
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

    public void start(Application application) throws IOException, RegistryException, InterruptedException, ExecutionException, CacheDirectoryCreationException, InvalidImageReferenceException {
        if (checkIfJarFileExists(application)) {

            if (!checkIfDockerImageExists(application)) { //vérifier le port auquel l'image est liée
                createContainer(application);
            }
            loadImage(application);
            runDockerImage(application);
            return;
        }
        throw new FileNotFoundException();
    }

    public void createContainer(Application application) throws IOException, InvalidImageReferenceException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
        Jib.from("openjdk:15")
                .addLayer(Arrays.asList(getPathToJarFile(application.getJarName())), AbsoluteUnixPath.get("/"))
                .setEntrypoint("java", "--enable-preview", "-jar", application.getJarName(), "--server.port=" + application.getPort())
                .containerize(Containerizer.to(TarImage.at(getPathToDockerImage(application.getName())).named(application.getName())));
    }

    public boolean checkIfDockerImageExists(Application application) {
        return Files.exists(getPathToDockerImage(application.getName()));
    }

    public boolean checkIfJarFileExists(Application application) {
        return Files.exists(getPathToJarFile(application.getJarName()));
    }

    public void loadImage(Application application) throws IOException {
        var loadCommand = new ProcessBuilder();
        loadCommand.command(os.getCMD(), os.getOption(), "docker load < " + application.getName());
        loadCommand.directory(new File(os.getParent() + os.getSeparator() + dockerImagesDirectoryName)); // on se place dans le répertoire des images
        loadCommand.start();
    }

    public void runDockerImage(Application application) throws IOException {
        var loadCommand = new ProcessBuilder();
        loadCommand.command(os.getCMD(), os.getOption(), "docker run -p " + application.getPort() + ":" + application.getPort() + " " + application.getName() + " &");
        //voir à donner un nom au container pour pouvoir faire "docker stop name"
        loadCommand.start();
    }

    public void stopDockerImage() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Path getPathToJarFile(String jarFilename) {
        return Paths.get(String.join(os.getSeparator(), os.getParent(), jarDirectoryName, jarFilename));
    }

    private Path getPathToDockerImage(String imageName) {
        return Paths.get(String.join(os.getSeparator(), os.getParent(), dockerImagesDirectoryName, imageName));
    }
}
