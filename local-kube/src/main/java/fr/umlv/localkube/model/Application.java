package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Objects;

public class Application {
    private static final int minPortService = 49152;
    private final long startTime = System.currentTimeMillis();
    /**
     * Application ID
     */
    @JsonProperty("id")
    private final int id;
    /**
     * Application name
     */
    @JsonProperty("app")
    private final String app;
    /**
     * Application public port
     */
    @JsonProperty("port")
    private final int portApp;
    /**
     * Application service/private port
     */
    @JsonProperty("service-port")
    private final int portService;
    /**
     * Docker instance name
     */
    @JsonProperty("docker-instance")
    private final String dockerInstance;
    /**
     * Elapsed time since application launch
     */
    @JsonProperty("elapsed-time")
    private String elapsedTime;

    public static Application initializeApp(String app, int id) throws IOException {
        if (id <= 0) {
            throw new IllegalArgumentException("id can't be negative");
        }
        Objects.requireNonNull(app);
        var portApp = getPortFromName(app);
        var portService = checkAndGetPortService();
        var dockerInstance = app.split(":")[0] + "_" + portApp;
        return new Application(id, app, portApp, portService, dockerInstance);
    }

    private Application(int id, String app, int portApp, int portService, String dockerInstance) {
        this.id = id;
        this.app = app;
        this.portApp = portApp;
        this.portService = portService;
        this.dockerInstance = dockerInstance;
    }

    private static int checkAndGetPortService() throws IOException {
        var port = minPortService;
        while (port < 65536) {
            try {
                port++;
                var s = new ServerSocket(port);
                s.close();
                break;
            } catch (IOException ignored){

            }
        }
        if (port > 65535) {
            throw new IOException("no free private port found");
        }
        return port;
    }

    @JsonIgnore
    public String getJarName() {
        return getName() + ".jar";
    }

    public int getId() {
        return id;
    }

    @JsonIgnore
    public String getName() {
        return app.split(":")[0];
    }

    public String getApp() {
        return app;
    }

    public int getPortService() {
        return portService;
    }

    public int getPortApp() {
        return portApp;
    }

    public String getDockerInstance() {
        return dockerInstance;
    }

    public String getElapsedTime() {
        return formatElapsedTime(System.currentTimeMillis());
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
