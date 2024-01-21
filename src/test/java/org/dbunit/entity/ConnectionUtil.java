package org.dbunit.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.ant.DbConfig;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.util.Assert;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public final class ConnectionUtil {

    public static IDatabaseConnection createConnection(JdbcConfig jdbcConfig) throws SQLException {
        validParameter(jdbcConfig);
        Driver driverInstance = getDriver(jdbcConfig.getDriverClass());
        Connection conn = getConnection(jdbcConfig, driverInstance);
        IDatabaseConnection databaseConnection = getDatabaseConnection(conn, jdbcConfig.getSchema());
        setDatabaseConfig(databaseConnection, jdbcConfig, new DbConfig(), new Future());
        return databaseConnection;
    }

    private static void validParameter(JdbcConfig jdbcConfig) {
        assert jdbcConfig != null;
        final String msg = "%s was not set";
        Assert.assertThat(StringUtils.isNotEmpty(jdbcConfig.getDriverClass()), new RuntimeException(String.format(msg, "driverClass")));
        Assert.assertThat(StringUtils.isNotEmpty(jdbcConfig.getUrl()), new RuntimeException(String.format(msg, "url")));
        Assert.assertThat(StringUtils.isNotEmpty(jdbcConfig.getUserId()), new RuntimeException(String.format(msg, "userId")));
        Assert.assertThat(StringUtils.isNotEmpty(jdbcConfig.getPassword()), new RuntimeException(String.format(msg, "password")));
    }

    private static Driver getDriver(String driverClass) {
        final String msg = "%s: JDBC driver %s could not be loaded";
        Driver driverInstance;
        try {
            Class<?> dc = Class.forName(driverClass);
            driverInstance = (Driver) dc.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format(msg, "ClassNotFoundException", driverClass), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format(msg, "IllegalAccessException", driverClass), e);
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format(msg, "InstantiationException", driverClass), e);
        }
        return driverInstance;
    }

    private static Connection getConnection(JdbcConfig jdbcConfig, Driver driverInstance) throws SQLException {
        String url = jdbcConfig.getUrl();
        Connection conn = driverInstance.connect(url, userAndPassword(jdbcConfig));
        if (conn == null) {
            throw new SQLException("No suitable driver for " + url);
        }
        conn.setAutoCommit(true);
        return conn;
    }

    private static Properties userAndPassword(JdbcConfig jdbcConfig) {
        Properties properties = new Properties();
        properties.put("user", jdbcConfig.getUserId());
        properties.put("password", jdbcConfig.getPassword());
        return properties;
    }

    private static IDatabaseConnection getDatabaseConnection(Connection conn, String schema) {
        IDatabaseConnection databaseConnection;
        try {
            databaseConnection = new DatabaseConnection(conn, schema);
        } catch (DatabaseUnitException e) {
            throw new RuntimeException("Could not create connection object", e);
        }
        return databaseConnection;
    }

    private static void setDatabaseConfig(IDatabaseConnection databaseConnection, JdbcConfig jdbcConfig, DbConfig dbConfig, Future future) {
        DatabaseConfig databaseConfig = databaseConnection.getConfig();
        if (dbConfig != null) {
            try {
                dbConfig.copyTo(databaseConfig);
            } catch (DatabaseUnitException e) {
                throw new RuntimeException("Could not populate dbConfig object", e);
            }
        }
        // For backwards compatibility (old mode overrides the new one) copy the other attributes to the config
        copyAttributes(databaseConfig, future, jdbcConfig.getDriverClass());
        log.info("Connection was created for schema '" + jdbcConfig.getSchema() + "' with config: " + databaseConfig);
    }

    /**
     * @deprecated since 2.4. Only here because of backwards compatibility should be removed in the next major release.
     */
    private static void copyAttributes(DatabaseConfig databaseConfig, Future future, String driverClass) {
        if (future.getSupportBatchStatement() != null) {
            databaseConfig.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, future.getSupportBatchStatement());
        }
        if (future.getUseQualifiedTableNames() != null) {
            databaseConfig.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, future.getUseQualifiedTableNames());
        }
        if (future.getDatatypeWarning() != null) {
            databaseConfig.setFeature(DatabaseConfig.FEATURE_DATATYPE_WARNING, future.getDatatypeWarning());
        }
        if (future.getSkipOracleRecycleBinTables() != null) {
            databaseConfig.setFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, future.getSkipOracleRecycleBinTables());
        }
        if (StringUtils.isNotEmpty(future.getEscapePattern())) {
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, future.getEscapePattern());
        }
        if (StringUtils.isNotEmpty(future.getBatchSize())) {
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, Integer.valueOf(future.getBatchSize()));
        }
        if (StringUtils.isNotEmpty(future.getFetchSize())) {
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_FETCH_SIZE, Integer.valueOf(future.getFetchSize()));
        }
        setDataTypeFactory(databaseConfig, future.getDataTypeFactory(), driverClass);
    }

    private static void setDataTypeFactory(DatabaseConfig databaseConfig, String dataTypeFactoryClazz, String driverClass) {
        if (StringUtils.isEmpty(dataTypeFactoryClazz)) {
            return;
        }
        // Setup data type factory
        final String msg = "%s: DataType factory %s could not be loaded";
        try {
            IDataTypeFactory dataTypeFactory = (IDataTypeFactory) Class.forName(dataTypeFactoryClazz).newInstance();
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format(msg, "ClassNotFoundException", driverClass), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format(msg, "IllegalAccessException", driverClass), e);
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format(msg, "InstantiationException", driverClass), e);
        }
    }
}