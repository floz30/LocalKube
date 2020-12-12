package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.umlv.localkube.services.ApplicationService;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // see here https://github.com/FasterXML/jackson-future-ideas/issues/46
public record Log(@ColumnName("id") int id,
                  @ColumnName("app") String app,
                  @ColumnName("port") int port,
                  @ColumnName("service-port") int portService,
                  @ColumnName("docker-instance") String dockerInstance,
                  @ColumnName("message") String message,
                  @ColumnName("timestamp") Instant timestamp) {

    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }

    public static class LogMapper implements RowMapper<Log> {

        private ApplicationService applicationService;

        public LogMapper(ApplicationService applicationService){
            this.applicationService =applicationService;
        }

        @Override
        public Log map(ResultSet rs, StatementContext ctx) throws SQLException {
            var application = applicationService.findById(rs.getInt(1)).orElseThrow();
            return new Log(rs.getInt(1),application.getApp(),application.getPortApp(),application.getPortService(),application.getDockerInstance(),rs.getString(2),rs.getTimestamp(3).toInstant());
        }
    }

}

