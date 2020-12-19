package fr.umlv.localkube.manager;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.utils.OperatingSystem;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * DockerManager class allows you to manage docker containers.
 * She can create container, start it, stop it or delete it.
 */
public class DockerManager {
    private final OperatingSystem os;
    private final DockerProperties properties;

    public DockerManager(OperatingSystem os, DockerProperties properties) throws IOException, InterruptedException {
        this.os = os;
        this.properties = properties;
        initializeSwarm();
    }

    /**
     * Starts a docker container if jar file link to application exists, and put this
     * application in container.
     *
     * @param application the application to put in new docker container
     * @throws IOException                     if an I/O exception occurs
     * @throws InvalidImageReferenceException  when attempting to parse an invalid image reference
     * @throws InterruptedException            if the execution was interrupted
     * @throws ExecutionException              if some other exception occurred during execution
     * @throws RegistryException               if some other error occurred while interacting with a registry
     * @throws CacheDirectoryCreationException if a directory to be used for the cache could not be created
     * @exception FileNotFoundException if jar file does not exist
     */
    public void startContainer(Application application) throws IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException, InvalidImageReferenceException {
        Objects.requireNonNull(application);
        if (checksIfJarFileExists(application)) {
            if (!checksIfDockerImageExists(application)) {
                createDockerContainer(application);
                loadImage(application);
            }
            runDockerImage(application);
            return;
        }
        throw new FileNotFoundException("application jar file not found for " + application);
    }

    /**
     * Stops container thanks to his name.
     *
     * @param application Application that must be stopped
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void stopContainer(Application application) throws IOException, InterruptedException {
        removeContainer(application.getDockerInstance());
    }

    /**
     * Stops and deletes all container still alive.
     *
     * @param names an array of application names
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void removeAll(String[] names) throws IOException, InterruptedException {
        for(String name : names){
            removeContainer(name);
        }
    }

    /**
     * Returns an array of all stopped container.
     * <p>
     * Call docker's {@code ps} command.
     *
     * @return an array of stopped container
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public String[] listDeadContainers() throws IOException, InterruptedException {
        var listCommand = new ProcessBuilder();
        listCommand.command(os.getCMD(), os.getOption(), "docker ps -f \"status=exited\" --format '{{.Names}}'");
        var names = testExitValue(listCommand.start());
        if(names.length() < 1){
            return new String[0];
        }
        return names.split("\n");
    }

    /**
     *
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void initializeSwarm() throws IOException, InterruptedException {
        var swarmInitCommand = new ProcessBuilder();
        swarmInitCommand.command(os.getCMD(), os.getOption(), "docker swarm init " + os.getWlo1IpAddress());
        testExitValue(swarmInitCommand.start());
    }

    @PreDestroy
    public void onShutdownLeaveSwarm() throws IOException, InterruptedException {
        var swarmLeaveCommand = new ProcessBuilder();
        swarmLeaveCommand.command(os.getCMD(), os.getOption(), "docker swarm leave --force ");
        testExitValue(swarmLeaveCommand.start());
    }

    /**
     * Load an image from a file in /docker-images/ directory. The image name is the application's name.
     * <p>
     * Call docker's {@code load} command on the specified application.
     *
     * @param application           the initialized application
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    private void loadImage(Application application) throws IOException, InterruptedException {
        var loadCommand = new ProcessBuilder();
        loadCommand.command(os.getCMD(), os.getOption(), "docker load < " + application.getName());
        loadCommand.directory(new File(getPathToDockerImage("").toString())); // on se place dans le répertoire des images
        testExitValue(loadCommand.start());
    }

    /**
     * Create a new docker container.
     *
     * @param application                      the initialized application
     * @throws IOException                     if an I/O exception occurs
     * @throws InvalidImageReferenceException  when attempting to parse an invalid image reference
     * @throws InterruptedException            if the execution was interrupted
     * @throws ExecutionException              if some other exception occurred during execution
     * @throws RegistryException               if some other error occurred while interacting with a registry
     * @throws CacheDirectoryCreationException if a directory to be used for the cache could not be created
     */
    private void createDockerContainer(Application application) throws InvalidImageReferenceException, IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
        Jib.from("openjdk:15")
                .addLayer(Arrays.asList(getPathToJarFile(application.getJarName()), getPathToLibrary()), AbsoluteUnixPath.get("/"))
                .containerize(Containerizer.to(TarImage.at(getPathToDockerImage(application.getName())).named(application.getName())));
    }

    /**
     * Checks if a docker image with specified name already exists in /docker-images/ directory.
     *
     * @param application Initialized application
     * @return true if image exists otherwise false
     */
    private boolean checksIfDockerImageExists(Application application) {
        return Files.exists(getPathToDockerImage(application.getName()));
    }

    /**
     * Checks if a jar file with specified name already exists in /apps/ directory.
     *
     * @param  application Initialized application
     * @return true if file exists otherwise false
     */
    private boolean checksIfJarFileExists(Application application) {
        return Files.exists(getPathToJarFile(application.getJarName()));
    }

    /**
     * Run a docker container on specified port and put application's jar file in container.
     * Call docker's {@code run} command on the specified application.
     *
     * @param application           the initialized application
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting,
     * then the wait is ended and an InterruptedException is thrown
     */
    private void runDockerImage(Application application) throws IOException, InterruptedException {
        var runCommand = new ProcessBuilder();
        runCommand.command(os.getCMD(),
                os.getOption(),
                String.join("", Arrays.asList("docker run ", os.getHostOption(), " --entrypoint java -d -p ", application.getPortApp() + ":8080 --name ", application.getDockerInstance(), " ", application.getName(), " -Dloader.path=. --enable-preview -jar ", application.getJarName(), " --service.port=" + application.getPortService())));
        testExitValue(runCommand.start());
    }

    /**
     * Call docker's {@code rm} command on the specified application name.
     *
     * @param name                  the application name to delete
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting,
     * then the wait is ended and an InterruptedException is thrown
     */
    private void removeContainer(String name) throws IOException, InterruptedException {
        var stopCommand = new ProcessBuilder();
        stopCommand.command(os.getCMD(), os.getOption(), " docker rm -f " + name);
        testExitValue(stopCommand.start());
    }

    private String testExitValue(Process process) throws InterruptedException, IOException {
        if (process.waitFor() != 0) {
            throw new IOException(readInputStream(process.getErrorStream()));
        }
        return readInputStream(process.getInputStream());
    }

    private String readInputStream(InputStream inputStream) {
        var errorReader = new BufferedReader(new InputStreamReader(inputStream));
        return errorReader.lines().collect(Collectors.joining());
    }

    /**
     * Build and return path to specified jar file.
     *
     * @param jarFilename String containing jar filename
     * @return the resulting path
     */
    private Path getPathToJarFile(String jarFilename) {
        return Paths.get(properties.getApps(), jarFilename);
    }

    private Path getPathToLibrary() {
        return Paths.get(properties.getLib());
    }

    /**
     * Build and return path to specified docker image.
     *
     * @param imageName the string containing docker image name
     * @return the resulting path
     */
    private Path getPathToDockerImage(String imageName) {
        return Paths.get(properties.getImages(), imageName);
    }


}
