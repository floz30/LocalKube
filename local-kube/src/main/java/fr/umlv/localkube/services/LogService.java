package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

public interface LogService {

    void insertLog(Log log);

//    List<Log> selectAllFromDuration(Duration minutes);
//
//    List<Log> selectAllFromDurationById(Duration minutes, int id);
//
//    List<Log> selectAllFromDurationByApp(Duration minutes, String app);

}
