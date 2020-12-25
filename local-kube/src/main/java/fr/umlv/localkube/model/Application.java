package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.utils.DockerContainer;
import fr.umlv.localkube.utils.DockerType;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Class which represents a docker application.
 */
public class Application {

    public interface View {
         interface OnStart {}
         interface OnListAndStop extends OnStart {}
    }

    private static final int MIN_PORT_SERVICE = 49152;
    private static final int MAX_PORT_SERVICE = 65535;
    private final long startTime = System.currentTimeMillis();

    /**
     * Application ID.
     */
    @JsonProperty("id")
    @JsonView(View.OnStart.class)
    private final int id;
    /**
     * Application name.
     */
    @JsonProperty("app")
    @JsonView(View.OnStart.class)
    private final String app;
    /**
     * Application public port.
     */
    @JsonProperty("port")
    @JsonView(View.OnStart.class)
    private final int portApp;
    /**
     * Application service/private port.
     */
    @JsonProperty("service-port")
    @JsonView(View.OnStart.class)
    private final int portService;
    /**
     * Docker instance name.
     */
    @JsonProperty("docker-instance")
    @JsonView(View.OnStart.class)
    private final String dockerInstance;
    /**
     * Elapsed time since application launch.
     */
    @JsonProperty("elapsed-time")
    @JsonView(View.OnListAndStop.class)
    private String elapsedTime;

    private DockerType dockerType = new DockerContainer();

    /**
     * Application is still alive ?
     */
    private boolean alive;

    /**
     * Creates and initializes a new application from his name and his ID.
     * This method will retrieve port from the name, calculate service port and
     * create the name for docker instance.
     *
     * @param app the name of this new application with his port separated by ":"
     * @param id  ID of this new application
     * @return    the new application with its fields initialized
     * @throws IllegalArgumentException if {@code ID} is negative or zero
     */
    public static Application initializeApp(String app, int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id can't be negative");
        }
        Objects.requireNonNull(app);
        var portApp = getPortFromName(app);
        var portService = getAvailablePortService();
        var dockerInstance = app.split(":")[0] + "_" + id;
        return new Application(id, app, portApp, portService, dockerInstance);
    }

    private Application(int id, String app, int portApp, int portService, String dockerInstance) {
        this.id = id;
        this.app = app;
        this.portApp = portApp;
        this.portService = portService;
        this.dockerInstance = dockerInstance;
        this.alive=true;
    }

    /**
     * Returns filename with .jar extension of this application.
     *
     * @return the filename of jar file
     * @see    #getName()
     */
    public String getJarName() {
        return getName() + ".jar";
    }

    /**
     * Returns ID of this application.
     *
     * @return ID of this application
     */
    public int getId() {
        return id;
    }

    /**
     * Returns name of this application without the port.
     *
     * @return the name of this application
     * @see    #getApp()
     */
    public String getName() {
        return app.split(":")[0];
    }

    /**
     * Returns name of this application with the port separated by ":".
     *
     * @return the name of this application with his port
     * @see    #getName()
     */
    public String getApp() {
        return app;
    }

    /**
     * Returns {@code true} if this application is still alive, otherwise returns {@code false}.
     *
     * @return true if the application is alive, false otherwise.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the the docker type to a new one
     * @param dockerType type of docker application
     */
    public void setDockerType(DockerType dockerType) {
        this.dockerType = dockerType;
    }

    /**
     * Returns service port of this application.
     *
     * @return the service port
     */
    public int getPortService() {
        return portService;
    }

    /**
     * Returns public port of this application.
     *
     * @return the public port
     */
    public int getPortApp() {
        return portApp;
    }

    /**
     * Returns the name of the docker instance of this application.
     *
     * @return the docker instance
     */
    public String getDockerInstance() {
        return dockerInstance;
    }

    /**
     * Returns elapsed time since this application started.
     *
     * @return the elapsed time
     */
    public String getElapsedTime() {
        elapsedTime = formatElapsedTime(System.currentTimeMillis());
        return elapsedTime;
    }

    /**
     * Removes the application from docker
     * @param dockerManager docker manager
     * @throws IOException if docker command fails
     * @throws InterruptedException if docker command fails
     */
    public void removeApplication(DockerManager dockerManager) throws IOException, InterruptedException {
        dockerType.removeApplication(this,dockerManager);
        alive = false;
    }

    /**
     * Scales the application in docker
     * @param numberOfInstance number of instance to run on scale
     * @param dockerManager docker manager
     * @throws IOException if docker command fails
     * @throws InterruptedException if docker command fails
     */
    public void scaleApplication(int numberOfInstance,DockerManager dockerManager) throws IOException, InterruptedException {
        dockerType.scaleApplication(this,numberOfInstance,dockerManager);
    }

    private static int getAvailablePortService() {
        return IntStream.range(MIN_PORT_SERVICE, MAX_PORT_SERVICE+1).filter(Application::testPortAvailability).findFirst().orElseThrow();
    }

    private static boolean testPortAvailability(int port) {
        try{
            new ServerSocket(port).close();
        }catch (IOException e){
            return false;
        }
        return true;
    }

    private static int getPortFromName(String app) {
        var appSplit = app.split(":");
        if(appSplit.length!=2){
            throw new IllegalArgumentException("Wrong format for app, must be <NAME>:<PORT> having :" + app);
        }
        var strPort = appSplit[1];
        return Integer.parseInt(strPort);
    }

    private String formatElapsedTime(long endTime) {
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime - startTime);
        return calendar.get(Calendar.MINUTE) + "m" + calendar.get(Calendar.SECOND) + "s";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Application a &&
                id == a.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", app='" + app + '\'' +
                ", port=" + portApp +
                ", service-port=" + portService +
                ", docker-instance='" + dockerInstance + '\'' +
                ", elapsed-time='" + getElapsedTime() + '\'' +
                '}';
    }
}
