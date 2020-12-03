package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Log;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository {

    /**
     * Create log table if doesn't exists.
     * If table already exists, delete all tuples.
     */
    default void init() {
        createTable();
        cleanTable();
    }

    /**
     * Create log table in database.
     */
    @SqlUpdate("CREATE TABLE IF NOT EXISTS log (id INTEGER PRIMARY KEY AUTOINCREMENT, app_id INTEGER NOT NULL, message TEXT NOT NULL, timestamp DATETIME CURRENT_TIMESTAMP NOT NULL)")
    boolean createTable();

    /**
     * Delete all tuples from log table.
     */
    @SqlUpdate("DELETE FROM log")
    void cleanTable();

    /**
     * Saves a given log.
     */
    @SqlUpdate("INSERT INTO log(app_id, message, timestamp) VALUES (?, ?, ?)")
    @RegisterConstructorMapper(Log.class)
    void save(int app_id, String message, Timestamp timestamp);

    /**
     * Returns all instances of log.
     * @return all logs
     */
    @SqlQuery("SELECT * FROM log")
    @RegisterConstructorMapper(Log.class)
    List<Log> findAll();

    /**
     * Retrieves a log by its id.
     * @param id id to search
     * @return the entity with the specified id or Optional.empty() if none found
     */
    @SqlQuery("SELECT * FROM log WHERE id = ?")
    @RegisterConstructorMapper(Log.class)
    Optional<Log> findById(int id);

//    List<Log> selectAllFromDuration(Duration minutes);
//
//    List<Log> selectAllFromDurationById(Duration minutes, int id);
//
//    List<Log> selectAllFromDurationByApp(Duration minutes, String app);

}
