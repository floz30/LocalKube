package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

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

    /**
     * Application is still alive ?
     */
    private boolean alive;

    /**
     * Builder is only use in unit tests.
     */
    public static class ApplicationBuilder {
        private int id;
        private String app;
        private int portApp;
        private int portService;
        private String dockerInstance;

        public Application build() {
            if (id <= 0) {
                throw new IllegalStateException("id can't be negative or equal to 0");
            }
            return new Application(id, app, portApp, portService, dockerInstance);
        }

        public Application buildRandom() {
            var id = new Random().nextInt(100_000) + 1; // pour éviter 0
            var portApp = new Random().nextInt(65536);
            var portService = new Random().nextInt(65536);
            var app = "hello:" + portApp;
            var dockerInstance = "hello_" + portApp;
            return new Application(id, app, portApp, portService, dockerInstance);
        }

        public ApplicationBuilder setId(int id) {
            this.id = id;
            return this;
        }
        public ApplicationBuilder setApp(String app) {
            this.app = app;
            return this;
        }
        public ApplicationBuilder setportApp(int portApp) {
            this.portApp = portApp;
            return this;
        }
        public ApplicationBuilder setportService(int portService) {
            this.portService = portService;
            return this;
        }
        public ApplicationBuilder setDockerInstance(String dockerInstance) {
            this.dockerInstance = dockerInstance;
            return this;
        }
    }

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
        var dockerInstance = app.split(":")[0] + "_" + portApp;
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
     * Kill this application.
     * @see #alive
     */
    public void kill(){
        this.alive = false;
    }

    /**
     * Returns {@code true} if this application is still alive, otherwise returns {@code false}.
     *
     * @return
     */
    public boolean isAlive() {
        return alive;
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
        var strPort = app.split(":")[1];
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
