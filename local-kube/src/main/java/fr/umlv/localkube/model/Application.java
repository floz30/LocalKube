package fr.umlv.localkube.model;

import java.util.Calendar;
import java.util.Objects;

public class Application {
    private static int COUNTER = 0;
    private final long startTime = System.currentTimeMillis();
    private final int id;
    private final String app; // nom de l'application
    private final int portApp; // port de l'application
    private final int portService; // port de discussion avec LocalKube
    private final String dockerInstance; // nom de l'instance du conteneur docker

    public Application(ApplicationDataRecord applicationDataRecord) {
        Objects.requireNonNull(applicationDataRecord.app());
        this.app = applicationDataRecord.app();
        this.id = ++COUNTER;
        this.portApp = getPortFromName();
        this.portService = 0;
        this.dockerInstance = getName() + "_" + portApp;
    }

    public ApplicationRecord toApplicationRecord() {
        return new ApplicationRecord(id, app, portApp, portService, dockerInstance, getElapsedTime());
    }

    public ApplicationDataRecord toApplicationStartRecord() {
        return new ApplicationDataRecord(id, app, portApp, portService, dockerInstance);
    }

    public String getJarName() {
        return getName() + ".jar";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return app.split(":")[0];
    }

    public int getPort() {
        return portApp;
    }

    private int getPortFromName() {
        var strPort = app.split(":")[1];
        return Integer.parseInt(strPort);
    }

    public String getDockerInstance() {
        return dockerInstance;
    }

    private String getElapsedTime() {
        return formatElapsedTime(System.currentTimeMillis());
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
