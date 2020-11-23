package fr.umlv.localkubeapi.consuming;

public interface LocalKubeConsumingFactory {
    LocalKubeConsuming create(int servicePort);
}
