package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

/**
 * Class which represents a Log sent from an application.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // see here https://github.com/FasterXML/jackson-future-ideas/issues/46
public record Log(@JsonProperty("id") @ColumnName("id") int id,
                  @JsonProperty("app") @ColumnName("app") String app,
                  @JsonProperty("port") @ColumnName("port") int port,
                  @JsonProperty("service-port") @ColumnName("service-port") int portService,
                  @JsonProperty("docker-instance") @ColumnName("docker-instance") String dockerInstance,
                  @JsonProperty("message") @ColumnName("message") String message,
                  @JsonProperty("timestamp") @ColumnName("timestamp") Instant timestamp) {

    public Log {
        if (id < 0) {
            throw new IllegalArgumentException("id >= 0 : " + id);
        }
        if (port < 0) {
            throw new IllegalArgumentException("port >= 0 : " + port);
        }
        if (portService < 0) {
            throw new IllegalArgumentException("portService >= 0 : " + portService);
        }
        Objects.requireNonNull(app);
        Objects.requireNonNull(dockerInstance);
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }

    /**
     * Specific Mapper for application log
     */
    public static class LogMapper implements RowMapper<Log> {

        private final ApplicationRepository applicationRepository;

        public LogMapper(ApplicationRepository applicationRepository){
            this.applicationRepository =applicationRepository;
        }

        /**
         * Maps a log retrieve from dataBase to a Log based on his application
         * @param rs the result set being iterated
         * @param ctx the statement context
         * @return the new log
         * @throws SQLException if jdbc mapping fails
         */
        @Override
        public Log map(ResultSet rs, StatementContext ctx) throws SQLException {
            var application = applicationRepository.findById(rs.getInt(1)).orElseThrow();
            return new Log(rs.getInt(1),application.getApp(),application.getPortApp(),application.getPortService(),application.getDockerInstance(),rs.getString(2),rs.getTimestamp(3).toInstant());
        }
    }

}

