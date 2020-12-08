package fr.umlv.localkube.manager;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.utils.OperatingSystem;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DockerManager {
    private final OperatingSystem os;
    private final DockerProperties properties;


    public DockerManager(OperatingSystem os, DockerProperties properties) {
        this.os = os;
        this.properties = properties;
    }

    public void start(Application application) throws IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException, InvalidImageReferenceException {
        if (checksIfJarFileExists(application)) {
            if (!checksIfDockerImageExists(application)) {
                createImage(application);
                loadImage(application);
            }
            runDockerImage(application);
            return;
        }
        throw new FileNotFoundException("application jar file not found for " + application);
    }

    /**
     * Create a new docker image
     *
     * @param application Initialized application
     * @throws IOException
     * @throws InvalidImageReferenceException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws RegistryException
     * @throws CacheDirectoryCreationException
     */
    public void createImage(Application application) throws InvalidImageReferenceException, IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
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
     * @param application Initialized application
     * @return true if file exists otherwise false
     */
    private boolean checksIfJarFileExists(Application application) {
        return Files.exists(getPathToJarFile(application.getJarName()));
    }

    /**
     * Load an image from a file in /docker-images/ directory.
     * The image name is the application's name
     *
     * @param application Initialized application
     * @throws IOException If an I/O error occurs
     */
    public void loadImage(Application application) throws IOException, InterruptedException {
        var loadCommand = new ProcessBuilder();
        loadCommand.command(os.getCMD(), os.getOption(), " docker load < " + application.getName());
        loadCommand.directory(new File(getPathToDockerImage("").toString())); // on se place dans le répertoire des images
        testExitValue(loadCommand.start());
    }

    /**
     * Run a docker on specified port.
     *
     * @param application Initialized application
     * @throws IOException
     * @throws InterruptedException
     */
    public void runDockerImage(Application application) throws IOException, InterruptedException {
        var runCommand = new ProcessBuilder();
        runCommand.command(os.getCMD(),
                os.getOption(),
                Arrays.asList("docker run ",os.getHostOption(), " --entrypoint java -d -p ", application.getPortApp() + ":8080 --name ", application.getDockerInstance(), " ", application.getName(), " -Dloader.path=. --enable-preview -jar ", application.getJarName() ," --service.port=" + application.getPortService()).stream().collect(Collectors.joining()));
        testExitValue(runCommand.start());
    }

    /**
     * Stops container thanks to his name.
     *
     * @param application Application that must be stopped
     * @throws IOException If an I/O error occurs
     */
    public void stopContainer(Application application) throws IOException, InterruptedException {
        removeContainer(application.getDockerInstance());
    }

    public void stopContainer(String name) throws IOException, InterruptedException {
        removeContainer(name);
    }

    private void removeContainer(String name) throws IOException, InterruptedException {
        var stopCommand = new ProcessBuilder();
        stopCommand.command(os.getCMD(), os.getOption(), " docker rm -f " + name);
        testExitValue(stopCommand.start());
    }

    public void removeAll(String[] names) throws IOException, InterruptedException {
        for(String name : names){
            removeContainer(name);
        }
    }

    public String[] listDeadContainers() throws IOException, InterruptedException {
        var listCommand = new ProcessBuilder();
        listCommand.command(os.getCMD(), os.getOption(), "docker ps -f \"status=exited\" --format '{{.Names}}'");
        var names = testExitValue(listCommand.start());
        if(names.length()<1){
            return new String[0];
        }
        return names.split("\n");
    }

    private String testExitValue(Process process) throws InterruptedException, IOException {
        if (process.waitFor() != 0) {
            throw new IOException(readInputStream(process.getErrorStream()));
        }
        return readInputStream(process.getInputStream());
    }

    private String readInputStream(InputStream inputStream) throws IOException {
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
     * @param imageName String containing docker image name
     * @return the resulting path
     */
    private Path getPathToDockerImage(String imageName) {
        return Paths.get(properties.getImages(), imageName);
    }
}
