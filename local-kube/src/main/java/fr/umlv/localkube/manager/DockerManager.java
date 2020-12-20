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

    /* Public methods */

    /**
     * Counts all services are running in the swarm.
     * <p>
     * Call {@code docker service ls} command.
     *
     * @param name the service name to search
     */
    public int countRunningTasks(String name) throws IOException, InterruptedException {
        var lsCommand = new ProcessBuilder()
                .command(os.getCMD(), os.getOption(), "docker service ls -f \"name=" + name + "\" --format \"{{.Replicas}}\"");
        var output = testExitValue(lsCommand.start());
        if (output.equals("")) {
            return 0;
        }
        return Integer.parseInt(output.split("/")[0]);
    }

    /**
     * Creates a new service with {@code numberOfReplicas} replicas.
     * <p>
     * Call {@code docker service create} command with replicas.
     *
     * @param application      the application that corresponds to the docker image used
     * @param numberOfReplicas number of replicas to create
     * @throws IOException if an I/O error occurs
     */
    public void createService(Application application, int numberOfReplicas) throws IOException {
        var command = String.join(" ",
                "docker service create",
                "--replicas " + numberOfReplicas,
                os.getHostOption("--host"),
                "--entrypoint java",
                "-d",
                "-p " + application.getPortApp() + ":8080",
                "--name " + application.getDockerInstance(),
                application.getName(),
                "-Dloader.path=.",
                "--enable-preview",
                "-jar " + application.getJarName(),
                "--service.port=" + application.getPortService()
        );

        createProcessBuilder(command).start();
    }

    /**
     * Returns an String array of all stopped container.
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
        if (names.length() < 1) {
            return new String[0];
        }
        return names.split("\n");
    }

    /**
     * Stop and delete all replicas when localkube is shutdown.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    @PreDestroy
    public void onShutdownLeaveSwarm() throws IOException, InterruptedException {
        var swarmLeaveCommand = new ProcessBuilder();
        swarmLeaveCommand.command(os.getCMD(), os.getOption(), "docker swarm leave --force");
        testExitValue(swarmLeaveCommand.start()); // à voir si on a besoin de testExitValue
    }

    /**
     * Stops and deletes all container still alive.
     *
     * @param names an array of application names
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void removeAll(String[] names) throws IOException, InterruptedException {
        for (String name : names) {
            removeContainer(name);
        }
    }

    /**
     * Stops and deletes the container with the specified {@code name}.
     * <p>
     * Call docker's {@code rm} command on the specified application name.
     *
     * @param name the application name to delete
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void removeContainer(String name) throws IOException, InterruptedException {
        testExitValue(createProcessBuilder("docker rm -f " + name).start());
    }

    /**
     * Stops and deletes the service with the specified {@code name}.
     * <p>
     * Call docker's {@code rm} command on the specified application name.
     *
     * @param name the application name to delete
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void removeService(String name) throws IOException, InterruptedException {
        testExitValue(createProcessBuilder(" docker service rm " + name).start());
    }

    /**
     * Scale one or more replicated services either up or down to the desired number of replicas.
     * <p>
     * Call {@code docker service scale} command.
     *
     * @param application   the application that corresponds to the docker image used
     * @param desiredNumber number of replicas
     * @throws IOException if an I/O error occurs
     */
    public void scaleService(Application application, int desiredNumber) throws IOException {
        createProcessBuilder(String.join(" ",
                "docker service scale",
                "-d",
                application.getDockerInstance() + "=" + desiredNumber)
        ).start();
    }

    /**
     * Starts the auto scale.
     * <p>
     * Call {@code docker node update} command.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void startAutoScale() throws IOException, InterruptedException {
        createProcessBuilder(String.join(" ",
                "docker node update",
                "--availability active " + getNodeName()));
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
     * @throws FileNotFoundException           if jar file does not exist
     */
    public void startContainer(Application application) throws IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException, InvalidImageReferenceException {
        Objects.requireNonNull(application);
        if (checksIfJarFileExists(application)) {
            if (!checksIfDockerImageExists(application)) {
                createDockerImage(application);
                loadImage(application);
            }
            createContainer(application);
            return;
        }
        throw new FileNotFoundException("application jar file not found for " + application);
    }

    /**
     * Stops the auto scale without shutdown applications.
     * <p>
     * Call {@code docker node} command with replicas.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void stopAutoScale() throws IOException, InterruptedException {
        createProcessBuilder(String.join(" ",
                "docker node update",
                "--availability pause" + getNodeName()));
    }

    /**
     * Stops container thanks to his name.
     *
     * @param application Application that must be stopped
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void stopContainer(Application application) throws IOException, InterruptedException {
        application.removeApplication(this);
    }


    /* Méthodes privées */


    private boolean checksIfDockerImageExists(Application application) {
        return Files.exists(getPathToDockerImage(application.getName()));
    }

    private boolean checksIfJarFileExists(Application application) {
        return Files.exists(getPathToJarFile(application.getJarName()));
    }

    private void createContainer(Application application) throws IOException, InterruptedException {
        var command = String.join(" ",
                "docker run",
                os.getHostOption("--add-host"),
                "--entrypoint java",
                "-d",
                "-p " + application.getPortApp() + ":8080",
                "--name " + application.getDockerInstance(),
                application.getName(),
                "-Dloader.path=.",
                "--enable-preview",
                "-jar " + application.getJarName(),
                "--service.port=" + application.getPortService()
        );
        testExitValue(createProcessBuilder(command).start());
    }

    private void createDockerImage(Application application) throws InvalidImageReferenceException, IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
        Jib.from("openjdk:15")
                .addLayer(Arrays.asList(getPathToJarFile(application.getJarName()), getPathToLibrary()), AbsoluteUnixPath.get("/"))
                .containerize(Containerizer.to(TarImage.at(getPathToDockerImage(application.getName())).named(application.getName())));
    }

    private ProcessBuilder createProcessBuilder(String command) {
        return new ProcessBuilder()
                .command(
                        os.getCMD(),
                        os.getOption(),
                        command
                );
    }

    private String getNodeName() throws IOException, InterruptedException {
        var command = createProcessBuilder(String.join(" ",
                "docker node ls",
                "--format \"{{.Hostname}}\""));
        return testExitValue(command.start());
    }

    private Path getPathToJarFile(String jarFilename) {
        return Paths.get(properties.getApps(), jarFilename);
    }

    private Path getPathToLibrary() {
        return Paths.get(properties.getLib());
    }

    private Path getPathToDockerImage(String imageName) {
        return Paths.get(properties.getImages(), imageName);
    }

    private void initializeSwarm() throws IOException, InterruptedException {
        var swarmInitCommand = new ProcessBuilder()
                .command(os.getCMD(), os.getOption(), "docker swarm init " + os.getWlo1IpAddress());
        testExitValue(swarmInitCommand.start());
    }

    private void loadImage(Application application) throws IOException, InterruptedException {
        var loadCommand = new ProcessBuilder()
                .command(os.getCMD(), os.getOption(), "docker load < " + application.getName())
                .directory(new File(getPathToDockerImage("").toString())); // on se place dans le répertoire des images
        testExitValue(loadCommand.start());
    }

    private String readInputStream(InputStream inputStream) {
        var errorReader = new BufferedReader(new InputStreamReader(inputStream));
        return errorReader.lines().collect(Collectors.joining());
    }

    private String testExitValue(Process process) throws InterruptedException, IOException {
        if (process.waitFor() != 0) {
            throw new IOException(readInputStream(process.getErrorStream()));
        }
        return readInputStream(process.getInputStream());
    }

}
