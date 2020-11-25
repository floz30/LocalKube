package com.example.demo;

import fr.umlv.localkubeapi.consuming.LocalKubeConsuming;
import fr.umlv.localkubeapi.consuming.LocalKubeConsumingFactory;

import java.util.ServiceLoader;

public class LocalKubeConsumingLoader {

    private final LocalKubeConsuming localKubeConsuming;

    public LocalKubeConsumingLoader(int servicePort){
        var serviceLoader = ServiceLoader.load(LocalKubeConsumingFactory.class);
        this.localKubeConsuming = serviceLoader.findFirst().orElseThrow().create(servicePort);
    }

    public void insertLog(String message){
        localKubeConsuming.insertLog(message);
    }

}
