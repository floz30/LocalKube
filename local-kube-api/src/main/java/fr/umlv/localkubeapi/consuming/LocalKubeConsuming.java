package fr.umlv.localkubeapi.consuming;

/**
 * Class able to communicate with LocalKube services
 */
public interface LocalKubeConsuming {

    /**
     * Inserts a log in LocalKube application
     * @param message message to log
     */
    void insertLog(String message);
}
