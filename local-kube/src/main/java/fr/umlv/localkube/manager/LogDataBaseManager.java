package fr.umlv.localkube.manager;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.utils.DataBaseProperties;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static void createTable(Jdbi jdbi) {
        jdbi.withHandle(handle ->
                handle.execute("CREATE TABLE IF NOT EXISTS log (id INTEGER PRIMARY KEY, app TEXT NOT NULL, port INTEGER NOT NULL, servicePort INTEGER NOT NULL, dockerInstance TEXT NOT NULL, message TEXT NOT NULL, timestamp DATE NOT NULL)")
        );
    }

    private static void cleanTable(Jdbi jdbi) {
        jdbi.withHandle(handle -> handle.execute("DELETE FROM log;"));
    }

    public void insertLog(int id, String app, int port, int servicePort, String dockerInstance, String message,String timestamp) {
        Objects.requireNonNull(app);
        Objects.requireNonNull(dockerInstance);
        Objects.requireNonNull(message);
        jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO log(id,app,port,servicePort,dockerInstance,message,timestamp) VALUES (:i,:a,:p,:s,:d,:m,:t)").bind("i", id).bind("a", app).bind("p", port).bind("s", servicePort).bind("d", dockerInstance).bind("m", message).bind("t",timestamp).execute()
        );
    }

    public List<Log> selectAllFromDuration(Duration minutes) {
        Objects.requireNonNull(minutes);
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes')").map(this::mapToLog).list());
    }

    public List<Log> selectAllFromDurationById(Duration minutes, int id) {
        Objects.requireNonNull(minutes);
        Objects.requireNonNull(id);
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes') AND id = " + id).map(this::mapToLog).list());
    }

    public List<Log> selectAllFromDurationByApp(Duration minutes, String app) {
        Objects.requireNonNull(minutes);
        Objects.requireNonNull(app);
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM log WHERE timestamp > DATETIME('now', '-" + minutes.toMinutes() + " minutes') AND app = '" + app + "'").map(this::mapToLog).list());
    }

    private Log mapToLog(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Log(rs.getInt("id"), rs.getString("app"), rs.getInt("port"), rs.getInt("servicePort"), rs.getString("dockerInstance"), rs.getString("message"), rs.getString("timestamp"));
    }



}
