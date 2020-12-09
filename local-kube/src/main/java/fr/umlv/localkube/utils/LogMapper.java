package fr.umlv.localkube.utils;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.ApplicationService;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogMapper implements RowMapper<Log> {


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
