package fr.umlv.localkube.repository;

import fr.umlv.localkube.manager.LogDataBaseManager;
import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.LogService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Repository
public class LogRepository implements LogService {

    private final LogDataBaseManager logManager;

    public LogRepository() throws IOException {
        this.logManager = LogDataBaseManager.initialize();;
    }

    @Override
    public void insertLog(int id, String app, int port, int servicePort, String dockerInstance, String message,String timestamp) {
        logManager.insertLog(id, app, port, servicePort, dockerInstance, message,timestamp);
    }

    @Override
    public List<Log> selectAllFromDuration(Duration minutes) {
        return logManager.selectAllFromDuration(minutes);
    }

    @Override
    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
        return logManager.selectAllFromDurationById(minutes, id);
    }

    @Override
    public List<Log> selectAllFromDurationByApp(Duration minutes, String app) {
        return logManager.selectAllFromDurationByApp(minutes, app);
    }
}
