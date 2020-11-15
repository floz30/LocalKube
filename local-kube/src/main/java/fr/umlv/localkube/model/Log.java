package fr.umlv.localkube.model;

public record Log(int id, String app, int port, int servicePort, String dockerInstance, String message,
                  String timestamp) {
}

