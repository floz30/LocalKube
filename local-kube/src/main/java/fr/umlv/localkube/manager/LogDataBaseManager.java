package fr.umlv.localkube.manager;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.utils.DataBaseProperties;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class LogDataBaseManager {

    private final Jdbi jdbi;

    private LogDataBaseManager(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public static LogDataBaseManager initialize() throws IOException {
        var properties = DataBaseProperties.credentialsFromPropertiesFile();
        var jdbi = Jdbi.create("jdbc:sqlite:" + properties.path(), properties.username(), properties.password());
        //.installPlugin(new SQLitePlugin());
        createTable(jdbi);
        cleanTable(jdbi);
        return new LogDataBaseManager(jdbi);
    }

    /**
     * Create log table in database.
     * @param jdbi
     */
    private static void createTable(Jdbi jdbi) {
        jdbi.withHandle(handle ->
                handle.execute("CREATE TABLE IF NOT EXISTS log (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "app_id INTEGER NOT NULL, " +
                        "message TEXT NOT NULL, " +
                        "timestamp DATETIME CURRENT_TIMESTAMP NOT NULL)")
        );
    }

    /**
     * Delete all tuples from log table.
     * @param jdbi
     */
    private static void cleanTable(Jdbi jdbi) {
        jdbi.withHandle(handle -> handle.execute("DELETE FROM log;"));
    }

    /**
     * Insert in log a new tuple.
     * @param app_id id of app
     * @param message content message to log
     * @param timestamp timestamp of creation
     */
    public void insertLog(int app_id, String message, Timestamp timestamp) {
        Objects.requireNonNull(message);
        jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO log(app_id,message,timestamp) VALUES (:a,:m,:t)")
                        .bind("a", app_id)
                        .bind("m", message)
                        .bind("t", timestamp).execute()
        );
    }

    public List<Log> selectAll() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log").map(this::mapToLog).list());
    }

    // à revoir car maintenant le timestamp est de type CURRENT_TIMESTAMP

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

    private Log mapToLog(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Log(rs.getInt("app_id"), rs.getString("message"), rs.getTimestamp("timestamp"));
    }



}
