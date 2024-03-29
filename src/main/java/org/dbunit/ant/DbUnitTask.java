/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.ant;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.util.Assert;
import org.dbunit.util.ResourceUtil;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <code>DbUnitTask</code> is the task definition for an Ant
 * interface to <code>DbUnit</code>.   DbUnit is a JUnit extension
 * which sets your database to a known state before executing your
 * tasks.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @version $Revision$
 * @see org.apache.tools.ant.Task
 * @since Jun 10, 2002
 */
@Slf4j
public class DbUnitTask extends Task {
    /**
     * Database connection
     */
    private Connection conn = null;

    /**
     * DB driver.
     */
    @Setter
    private String driver = null;

    /**
     * DB url.
     */
    @Setter
    private String url = null;

    /**
     * User name.
     */
    @Setter
    private String userid = null;

    /**
     * Password
     */
    @Setter
    private String password = null;

    /**
     * DB schema.
     */
    @Setter
    private String schema = null;

    /**
     * Steps
     */
    @Getter
    private final List<Object> steps = new ArrayList<>();

    private Path classpath;

    /**
     * DB configuration child element to configure {@link DatabaseConfig} properties
     * in a generic way.
     */
    @Getter
    private DbConfig dbConfig;

    /**
     * Flag for using the qualified table names.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean useQualifiedTableNames = null;

    /**
     * Flag for using batched statements.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean supportBatchStatement = null;

    /**
     * Flag for datatype warning.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean datatypeWarning = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private String escapePattern = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private String dataTypeFactory = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Getter
    @Setter
    private String batchSize = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Getter
    @Setter
    private String fetchSize = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean skipOracleRecycleBinTables = null;

    public void addDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * Set the classpath for loading the driver.
     */
    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    /**
     * Create the classpath for loading the driver.
     */
    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    /**
     * Set the classpath for loading the driver using the classpath reference.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * Adds an Operation.
     */
    public void addOperation(Operation operation) {
        steps.add(operation);
    }

    /**
     * Adds a Compare to the steps List.
     */
    public void addCompare(Compare compare) {
        steps.add(compare);
    }

    /**
     * Adds an Export to the steps List.
     */
    public void addExport(Export export) {
        steps.add(export);
    }

    /**
     * Load the step and then execute it
     */
    public void execute() throws BuildException {
        try {
            IDatabaseConnection connection = createConnection();
            for (Object o : steps) {
                DbUnitTaskStep step = (DbUnitTaskStep) o;
                log(step.getLogMessage(), Project.MSG_INFO);
                step.execute(connection);
            }
        } catch (DatabaseUnitException | SQLException e) {
            throw new BuildException(e, getLocation());
        } finally {
            ResourceUtil.releaseSilently(conn);
        }
    }

    protected IDatabaseConnection createConnection() throws SQLException {
        Assert.assertThat(driver != null, new BuildException("Driver must be set!", getLocation()));
        Assert.assertThat(userid != null, new BuildException("UserId must be set!", getLocation()));
        Assert.assertThat(password != null, new BuildException("Password must be set!", getLocation()));
        Assert.assertThat(url != null, new BuildException("Url must be set!", getLocation()));
        Assert.assertThat(steps.size() > 0, new BuildException("Must declare at least one step in a <dbunit> task!", getLocation()));

        Driver driverInstance = getDriver();
        log("connecting to " + url, Project.MSG_VERBOSE);

        Properties info = new Properties();
        info.put("user", userid);
        info.put("password", password);
        conn = driverInstance.connect(url, info);
        if (conn == null) {
            throw new SQLException("No suitable Driver for " + url);
        }
        conn.setAutoCommit(true);
        return createDatabaseConnection(conn, schema);
    }

    private Driver getDriver() {
        Driver driverInstance;
        try {
            Class<?> dc;
            if (classpath != null) {
                log("Loading " + driver + " using AntClassLoader with classpath " + classpath, Project.MSG_VERBOSE);
                AntClassLoader loader = new AntClassLoader(getProject(), classpath);
                dc = loader.loadClass(driver);
            } else {
                log("Loading " + driver + " using system loader.", Project.MSG_VERBOSE);
                dc = Class.forName(driver);
            }
            driverInstance = (Driver) dc.newInstance();
        } catch (ClassNotFoundException e) {
            throw new BuildException("Class Not Found: JDBC driver " + driver + " could not be loaded", e, getLocation());
        } catch (IllegalAccessException e) {
            throw new BuildException("Illegal Access: JDBC driver " + driver + " could not be loaded", e, getLocation());
        } catch (InstantiationException e) {
            throw new BuildException("Instantiation Exception: JDBC driver " + driver + " could not be loaded", e, getLocation());
        }
        return driverInstance;
    }

    /**
     * Creates the dbunit connection using the two given arguments. The configuration
     * properties of the dbunit connection are initialized using the fields of this class.
     *
     * @return The dbunit connection
     */
    protected IDatabaseConnection createDatabaseConnection(Connection jdbcConnection, String dbSchema) {
        log.trace("createDatabaseConnection(jdbcConnection={}, dbSchema={}) - start", jdbcConnection, dbSchema);

        IDatabaseConnection connection;
        try {
            connection = new DatabaseConnection(jdbcConnection, dbSchema);
        } catch (DatabaseUnitException e) {
            throw new BuildException("Could not create dbunit connection object", e);
        }

        DatabaseConfig config = connection.getConfig();
        if (dbConfig != null) {
            try {
                this.dbConfig.copyTo(config);
            } catch (DatabaseUnitException e) {
                throw new BuildException("Could not populate dbunit config object", e, getLocation());
            }
        }

        // For backwards compatibility (old mode overrides the new one) copy the other attributes to the config
        copyAttributes(config);
        log("Created connection for schema '" + schema + "' with config: " + config, Project.MSG_VERBOSE);
        return connection;
    }

    /**
     * @deprecated since 2.4. Only here because of backwards compatibility should be removed in the next major release.
     */
    private void copyAttributes(DatabaseConfig config) {
        if (supportBatchStatement != null) {
            config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, supportBatchStatement);
        }
        if (useQualifiedTableNames != null) {
            config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, useQualifiedTableNames);
        }
        if (datatypeWarning != null) {
            config.setFeature(DatabaseConfig.FEATURE_DATATYPE_WARNING, datatypeWarning);
        }
        if (skipOracleRecycleBinTables != null) {
            config.setFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, skipOracleRecycleBinTables);
        }
        if (escapePattern != null) {
            config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, escapePattern);
        }
        if (batchSize != null) {
            Integer batchSizeInteger = new Integer(batchSize);
            config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, batchSizeInteger);
        }
        if (fetchSize != null) {
            config.setProperty(DatabaseConfig.PROPERTY_FETCH_SIZE, new Integer(fetchSize));
        }

        // Setup data type factory
        if (dataTypeFactory != null) {
            try {
                IDataTypeFactory dataTypeFactory = (IDataTypeFactory) Class.forName(this.dataTypeFactory).newInstance();
                config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
            } catch (ClassNotFoundException e) {
                throw new BuildException("Class Not Found: DataType factory " + driver + " could not be loaded", e, getLocation());
            } catch (IllegalAccessException e) {
                throw new BuildException("Illegal Access: DataType factory " + driver + " could not be loaded", e, getLocation());
            } catch (InstantiationException e) {
                throw new BuildException("Instantiation Exception: DataType factory " + driver + " could not be loaded", e, getLocation());
            }
        }
    }
}