package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
