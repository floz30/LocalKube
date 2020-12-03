package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Log;


public interface LogRepository {

    /**
     * Saves a given log.
     * @param log log to save
     */
    void save(Log log);

    /**
     * Returns all instances of log.
     * @return all logs
     */
    Iterable<Log> findAll();

//    List<Log> selectAllFromDuration(Duration minutes);
//
//    List<Log> selectAllFromDurationById(Duration minutes, int id);
//
//    List<Log> selectAllFromDurationByApp(Duration minutes, String app);

}
