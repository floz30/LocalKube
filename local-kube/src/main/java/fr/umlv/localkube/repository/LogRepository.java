package fr.umlv.localkube.repository;

import fr.umlv.localkube.manager.LogDataBaseManager;
import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.LogService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

@Repository
public class LogRepository implements LogService {

    private final LogDataBaseManager logManager;

    public LogRepository() throws IOException {
        this.logManager = LogDataBaseManager.initialize();;
    }

    @Override
    public void insertLog(Log log) {
        logManager.insertLog(log.appId(), log.message(), log.timestamp());
    }

    public List<Log> selectAll() {
        return logManager.selectAll();
    }

//    @Override
//    public List<Log> selectAllFromDuration(Duration minutes) {
//        return logManager.selectAllFromDuration(minutes);
//    }
//
//    @Override
//    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
//        return logManager.selectAllFromDurationById(minutes, id);
//    }
//
//    @Override
//    public List<Log> selectAllFromDurationByApp(Duration minutes, String app) {
//        return logManager.selectAllFromDurationByApp(minutes, app);
//    }
}
