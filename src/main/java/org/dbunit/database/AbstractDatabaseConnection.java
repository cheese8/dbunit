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

package org.dbunit.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.util.QualifiedTableName;
import org.dbunit.util.SQLHelper;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 6, 2002
 */
@Slf4j
public abstract class AbstractDatabaseConnection implements IDatabaseConnection {
    private IDataSet dataSet = null;
    private final DatabaseConfig databaseConfig;

    public AbstractDatabaseConnection() {
        databaseConfig = new DatabaseConfig();
    }

    public IDataSet createDataSet() throws SQLException {
        if (dataSet == null) {
            dataSet = new DatabaseDataSet(this);
        }
        return dataSet;
    }

    public IDataSet createDataSet(String[] tableNames) throws DataSetException, SQLException {
        return new FilteredDataSet(tableNames, createDataSet());
    }

    public ITable createQueryTable(String resultName, String sql) throws DataSetException, SQLException {
        IResultSetTableFactory tableFactory = getResultSetTableFactory();
        IResultSetTable rsTable = tableFactory.createTable(resultName, sql, this);
        if (log.isDebugEnabled()) {
            String rowCount;
            try {
                int rowCountInt = rsTable.getRowCount();
                rowCount = String.valueOf(rowCountInt);
            } catch (Exception e) {
                rowCount = "Unable to determine row count due to Exception: " + e.getLocalizedMessage();
            }
            log.debug("createQueryTable: rowCount={}", rowCount);
        }
        return rsTable;
    }

    public ITable createTable(String resultName, PreparedStatement preparedStatement) throws DataSetException, SQLException {
        IResultSetTableFactory tableFactory = getResultSetTableFactory();
        return tableFactory.createTable(resultName, preparedStatement, this);
    }

    public ITable createTable(String tableName) throws DataSetException, SQLException {
        if (tableName == null) {
            throw new NullPointerException("The parameter 'tableName' must not be null");
        }
        String escapePattern = (String) getConfig().getProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN);
        // qualify with schema if configured
        QualifiedTableName qualifiedTableName = new QualifiedTableName(tableName, this.getSchema(), escapePattern);
        String qualifiedName = qualifiedTableName.getQualifiedName();
        String sql = "select * from " + qualifiedName;
        return this.createQueryTable(tableName, sql);
    }

    public int getRowCount(String tableName) throws SQLException {
        return getRowCount(tableName, null);
    }

    public int getRowCount(String tableName, String whereClause) throws SQLException {
        StringBuilder sqlBuffer = new StringBuilder(128);
        sqlBuffer.append("select count(*) from ");

        // add table name and schema (schema only if available)
        QualifiedTableName qualifiedTableName = new QualifiedTableName(tableName, this.getSchema());
        String qualifiedName = qualifiedTableName.getQualifiedName();
        sqlBuffer.append(qualifiedName);
        if (whereClause != null) {
            sqlBuffer.append(" ");
            sqlBuffer.append(whereClause);
        }

        Statement statement = getConnection().createStatement();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sqlBuffer.toString());
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new DatabaseUnitRuntimeException("Select count did not return any results for table '" + tableName + "'. Statement: " + sqlBuffer);
            }
        } finally {
            SQLHelper.close(resultSet, statement);
        }
    }

    public DatabaseConfig getConfig() {
        return databaseConfig;
    }

    /**
     * @deprecated Use {@link #getConfig}
     */
    @Deprecated
    public IStatementFactory getStatementFactory() {
        return (IStatementFactory) databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
    }

    private IResultSetTableFactory getResultSetTableFactory() {
        return (IResultSetTableFactory) databaseConfig.getProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY);

    }

    @Override
    public String toString() {
        return "databaseConfig=" + databaseConfig + ", dataSet=" + dataSet;
    }
}