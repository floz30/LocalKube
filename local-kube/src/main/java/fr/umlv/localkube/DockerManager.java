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
        if (checksIfJarFileExists(application)) {
            if (!checksIfDockerImageExists(application)) { //vérifier le port auquel l'image est liée
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

    /**
     * Checks if a docker image with specified name already exists in /docker-images/ directory.
     * @param application Initialized application.
     * @return true if image exists otherwise false.
     */
    public boolean checksIfDockerImageExists(Application application) {
        return Files.exists(getPathToDockerImage(application.getName()));
    }

    /**
     * Checks if a jar file with specified name already exists in /apps/ directory.
     * @param application Initialized application.
     * @return true if file exists otherwise false.
     */
    public boolean checksIfJarFileExists(Application application) {
        return Files.exists(getPathToJarFile(application.getJarName()));
    }

    /**
     * Load an image from a file in /docker-images/ directory.
     * The image name is the application's name.
     * @param application Initialized application.
     * @throws IOException If an I/O error occurs.
     */
    public void loadImage(Application application) throws IOException {
        var loadCommand = new ProcessBuilder();
        loadCommand.command(os.getCMD(), os.getOption(), "docker load < " + application.getName());
        loadCommand.directory(new File(os.getParent() + os.getSeparator() + dockerImagesDirectoryName)); // on se place dans le répertoire des images
        loadCommand.start();
    }

    public void runDockerImage(Application application) throws IOException {
        var runCommand = new ProcessBuilder();
        runCommand.command(os.getCMD(), os.getOption(), "docker run -d -p " + application.getPort() + ":" + application.getPort() + " --name " + application.getDockerInstance() + " " + application.getName());
        // permet de rediriger input/ouput/error dans celui du programme local-kube
        // avec l'option -d on affiche le container ID à stocker dans l'objet application
        // à voir si on peut pas le récupérer autrement que de l'afficher dans la sortie standard
        runCommand.inheritIO();
        runCommand.start();
    }

    /**
     * Stops container thanks to his name.
     * @param application Application that must be stopped.
     * @throws IOException If an I/O error occurs.
     */
    public void stopContainer(Application application) throws IOException {
        var stopCommand = new ProcessBuilder();
        stopCommand.command(os.getCMD(), os.getOption(), "docker stop " + application.getDockerInstance());
        stopCommand.start();
    }

    public void listAllContainers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Path getPathToJarFile(String jarFilename) {
        return Paths.get(String.join(os.getSeparator(), os.getParent(), jarDirectoryName, jarFilename));
    }

    private Path getPathToDockerImage(String imageName) {
        return Paths.get(String.join(os.getSeparator(), os.getParent(), dockerImagesDirectoryName, imageName));
    }
}
