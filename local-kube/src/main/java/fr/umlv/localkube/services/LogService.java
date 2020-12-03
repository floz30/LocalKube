package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;

import java.util.List;

public interface LogService {

    void insertLog(Log log);

    List<Log> selectAll();

//    List<Log> selectAllFromDuration(Duration minutes);
//
//    List<Log> selectAllFromDurationById(Duration minutes, int id);
//
//    List<Log> selectAllFromDurationByApp(Duration minutes, String app);

}
