package fr.umlv.localkubeapi.consuming;

public class ConsumingLogFactory implements  LocalKubeConsumingFactory{
    @Override
    public LocalKubeConsuming create(int servicePort) {
        return new ConsumingLog(servicePort);
    }
}
