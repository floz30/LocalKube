package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LogService {
    private final Jdbi dataBase;
    private final LogRepository repository;

    private LogService(Jdbi dataBase, LogRepository repository){
        this.dataBase = dataBase;
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        repository.init();
    }

    /**
     * Saves a given log.
     */
    public void save(int appId, String message, Instant timestamp) {
        repository.save(appId, message, timestamp);
    }

    /**
     * Returns all instances of log.
     * @return all logs
     */
    public List<Log> findAll() {
        return repository.findAll();
    }

    public List<Log> selectAllFromDuration(Duration minutes) {
        return repository.findAll(subtractMinutesToCurrentTime(minutes));
    }

    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
        return repository.findAllFilterById(subtractMinutesToCurrentTime(minutes), id);
    }

    private Instant subtractMinutesToCurrentTime(Duration minutes){
        return Instant.now().minusMillis(minutes.toMillis());
    }

}
