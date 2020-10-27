package fr.umlv.localkube.model;

import java.util.Objects;

public class Application  {
    //private final AtomicInteger counter = new AtomicInteger();
    private long id;
    private long dockerId; // numéro unique de l'instance docker
    private String app; // nom de l'application
    private int portApp; // port de l'application
    private int portService; // port de discussion avec LocalKube
    private String dockerInstance; // nom de l'instance du conteneur docker

    public Application() {

    }

    public Application(String app) {
        System.out.println("toto");
        Objects.requireNonNull(app);
        this.app = app;
//        portApp = getPortFromName(app);
//        this.id = 0;//counter.incrementAndGet();
    }

    private int getPortFromName(String name) {
        System.out.println(name);
        var strPort = name.split(":")[1];
        return Integer.parseInt(strPort);
    }

    public long getId() {
        return id;
    }
    public long getDockerId() {
        return dockerId;
    }
    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
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

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", dockerId=" + dockerId +
                ", name='" + app + '\'' +
                ", portApp=" + portApp +
                ", portService=" + portService +
                ", dockerInstance='" + dockerInstance + '\'' +
                '}';
    }
}
