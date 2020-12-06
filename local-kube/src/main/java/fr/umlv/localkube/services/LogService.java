package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {
    private final Jdbi db;
    private final LogRepository repository;

    private LogService(Jdbi db, LogRepository repository){
        this.db = db;
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
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

    //TODO ajouter le filtrage des logs (GET /logs/:time), (GET /logs/:time/:filter) et (GET /logs/:time/:filter/:value)

    public List<Log> selectAllFromDuration(int minutes) {
        var timestamp = LocalDateTime.now().minusMinutes(minutes).toString();
        return repository.findAll(timestamp);
    }

    public List<Log> selectAllFromDurationById(int minutes, int id) {
        var timestamp = LocalDateTime.now().minusMinutes(minutes).toString();
        return repository.findAllFilterById(timestamp, id);
    }
    //
    //    public List<Log> selectAllFromDurationByApp(Duration minutes, String app) {
    //        Objects.requireNonNull(minutes);
    //        Objects.requireNonNull(app);
    //        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes') AND app = '" + app + "'").map(this::mapToLog).list());
    //    }
}
