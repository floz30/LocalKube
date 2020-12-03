package fr.umlv.localkube.services;

import fr.umlv.localkube.configuration.DataBaseProperties;
import fr.umlv.localkube.manager.LogDataBaseManager;
import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogService implements LogRepository {
    private final LogDataBaseManager logManager;

    public LogService(DataBaseProperties properties){
        this.logManager = LogDataBaseManager.initialize(properties);
    }

    @Override
    public void save(Log log) {
        logManager.insertLog(log.appId(), log.message(), log.timestamp());
    }

    @Override
    public List<Log> findAll() {
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
