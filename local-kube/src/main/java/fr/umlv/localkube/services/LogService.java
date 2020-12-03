package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LogService {
    private final Jdbi db;
    private final LogRepository repository;

    private LogService(Jdbi db, LogRepository repository){
        this.db = db;
        this.repository = repository;
        repository.init();
    }

    /**
     * Saves a given log.
     */
    public void save(int appId, String message, Timestamp timestamp) {
        repository.save(appId, message, timestamp);
    }

    /**
     * Returns all instances of log.
     * @return all logs
     */
    public List<Log> findAll() {
        return repository.findAll();
    }

    //    public List<Log> selectAllFromDuration(Duration minutes) {
    //        Objects.requireNonNull(minutes);
    //        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes')").map(this::mapToLog).list());
    //    }
    //
    //    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
    //        Objects.requireNonNull(minutes);
    //        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes') AND id = " + id).map(this::mapToLog).list());
    //    }
    //
    //    public List<Log> selectAllFromDurationByApp(Duration minutes, String app) {
    //        Objects.requireNonNull(minutes);
    //        Objects.requireNonNull(app);
    //        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes') AND app = '" + app + "'").map(this::mapToLog).list());
    //    }
}
