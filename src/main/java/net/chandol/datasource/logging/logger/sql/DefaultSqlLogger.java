package net.chandol.datasource.logging.logger.sql;

import net.chandol.datasource.config.LoggableDataSourceConfig;
import net.chandol.datasource.sql.SqlParmeterBinder;
import net.chandol.datasource.sql.parameter.Parameter;
import net.chandol.datasource.sql.parameter.ParameterCollector;
import net.chandol.datasource.sql.parameter.converter.ParameterConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultSqlLogger implements SqlLogger {
    private static final Logger sqlLogger = LoggerFactory.getLogger("net.chandol.jdbc.sql");
    private static final Logger paramLogger = LoggerFactory.getLogger("net.chandol.jdbc.parameter");

    @Override
    public void logSql(LoggableDataSourceConfig config, String templateSql, ParameterCollector parameterCollector) {
        ParameterConverter converter = config.getConverter();
        List<Parameter> params = parameterCollector.getAll();
        List<String> convertedParams = converter.convert(params);

        // Parameter
        paramLogger.debug(parameterToLog(params, convertedParams));

        // SQL with formatter
        String sql = SqlParmeterBinder.bind(templateSql, convertedParams);
        String formattedSql = config.getFormatter().format(sql);

        sqlLogger.debug(formattedSql);
    }

    @Override
    public void logSql(LoggableDataSourceConfig config, String sql) {
        //SQL Formatting
        String formattedSql = config.getFormatter().format(sql);

        sqlLogger.debug(formattedSql);
    }

    // 파라미터가 모호함... 리팩토링 필요!!
    static String parameterToLog(List<Parameter> params, List<String> convertedParams) {
        StringBuilder builder = new StringBuilder();
        builder.append("parameters : [");
        for (int idx = 0; idx < params.size(); idx++) {
            String type = params.get(idx).getType().getTypeAsStr();
            String value = convertedParams.get(idx);

            builder.append("{").append(type).append(" = ").append(value).append("}");

            if ((params.size() - 1) != idx)
                builder.append(", ");
        }
        builder.append("]");

        return builder.toString();
    }
}
