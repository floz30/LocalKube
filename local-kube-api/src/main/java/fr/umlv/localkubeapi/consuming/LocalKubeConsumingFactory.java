package fr.umlv.localkubeapi.consuming;

/**
 * Factory creating a LocalKubeConsuming for sending information to LocalKube through a specific service port.
 */
public interface LocalKubeConsumingFactory {
    /**
     * Creates a LocalKubeConsuming using a specific service port.
     * @param servicePort service port for talking with LocalKube
     * @return LocalKubeConsuming
     */
    LocalKubeConsuming create(int servicePort);
}
