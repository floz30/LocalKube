package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.*;

import java.util.Calendar;
import java.util.Objects;

@JsonPropertyOrder({"id", "app", "portApp", "portService", "dockerInstance", "elapsedTime"})
public class Application  {
    private static long COUNTER = 0;
    private final long startTime = System.currentTimeMillis();
    private final long id;
    private final String app; // nom de l'application
    @JsonProperty("port")
    private final int portApp; // port de l'application
    @JsonProperty("port-service")
    private final int portService; // port de discussion avec LocalKube
    @JsonProperty(value="docker-instance")
    private final String dockerInstance; // nom de l'instance du conteneur docker
    @JsonProperty(value="elapsed-time")
    private String elapsedTime;

    @JsonCreator(mode= JsonCreator.Mode.PROPERTIES)
    public Application(@JsonProperty("app") String app) {
        Objects.requireNonNull(app);
        this.app = app;
        this.id = ++COUNTER;
        this.portApp = getPortFromName(app);
        this.portService = 0;
        this.dockerInstance = "nom docker";
        setElapsedTime(); // a revoir
    }

    private int getPortFromName(String name) {
        System.out.println(name);
        var strPort = name.split(":")[1];
        return Integer.parseInt(strPort);
    }

    public void setElapsedTime() {
        elapsedTime = formatElapsedTime(System.currentTimeMillis());
    }

    private String formatElapsedTime(long endTime) {
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime - startTime);
        return "" + calendar.get(Calendar.MINUTE) + "m" + calendar.get(Calendar.SECOND) + "s";
    }

    public long getId() {
        return id;
    }
    public String getApp() {
        return app;
    }
    public int getPortApp() {
        return portApp;
    }
    public int getPortService() {
        return portService;
    }
    public String getDockerInstance() {
        return dockerInstance;
    }
    public String getElapsedTime() {
        return elapsedTime;
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
                ", elapsed-time='" + elapsedTime + '\'' +
                '}';
    }
}
