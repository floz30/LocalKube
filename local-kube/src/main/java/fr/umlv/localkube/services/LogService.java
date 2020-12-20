package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LogService {
    private final LogRepository repository;

    private LogService(LogRepository repository){
        this.repository = repository;
        this.repository.init();
    }

    /**
     * Saves a given log.
     *
     * @param appId     ID of the application that issued this log
     * @param message   message to save
     * @param timestamp timestamp the log was issued
     */
    public void save(int appId, String message, Instant timestamp) {
        repository.save(appId, message, timestamp);
    }

    /**
     * Returns all instances of log.
     *
     * @return a list of {@code Log}
     */
    public List<Log> findAll() {
        return repository.findAll();
    }

    /**
     * Returns all logs who was issued since last {@code time} minutes.
     *
     * @param minutes
     * @return a list of {@code Log}
     */
    public List<Log> selectAllFromDuration(Duration minutes) {
        return repository.findAll(subtractMinutesToCurrentTime(minutes));
    }

    /**
     * Returns all logs who was issued since last {@code time} minutes for the application with its ID equals to {@code id}.
     *
     * @param minutes
     * @param id ID of the application
     * @return a list of {@code Log}
     */
    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
        return repository.findAllFilterById(subtractMinutesToCurrentTime(minutes), id);
    }

    private Instant subtractMinutesToCurrentTime(Duration minutes){
        return Instant.now().minusMillis(minutes.toMillis());
    }

}
