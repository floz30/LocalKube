package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;

import java.time.Duration;
import java.util.List;

public interface LogService {

    void insertLog(int id, String app, int port, int servicePort, String dockerInstance, String message,String timestamp);

    List<Log> selectAllFromDuration(Duration minutes);

    List<Log> selectAllFromDurationById(Duration minutes, int id);

    List<Log> selectAllFromDurationByApp(Duration minutes, String app);

}
