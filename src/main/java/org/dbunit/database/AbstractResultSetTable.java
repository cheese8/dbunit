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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;

/**
 * @author Manuel Laflamme
 * @since Apr 10, 2003
 * @version $Revision$
 */
public abstract class AbstractResultSetTable extends AbstractTable implements IResultSetTable {
    protected ITableMetaData metaData;
    private Statement statement;
    protected ResultSet resultSet;

    public AbstractResultSetTable(ITableMetaData metaData, ResultSet resultSet) {
        this.metaData = metaData;
        this.resultSet = resultSet;
    }

    public AbstractResultSetTable(String tableName, String selectStatement, IDatabaseConnection connection) throws DataSetException, SQLException {
        this(tableName, selectStatement, connection, false);
    }

    public AbstractResultSetTable(String tableName, String selectStatement, IDatabaseConnection connection, boolean caseSensitiveTableNames) throws DataSetException, SQLException {
        statement = createStatement(connection);
        try {
            resultSet = statement.executeQuery(selectStatement);
            metaData = new ResultSetTableMetaData(tableName, resultSet, connection, caseSensitiveTableNames);
        } catch (SQLException e) {
            statement.close();
            statement = null;
            throw e;
        }
    }

	public AbstractResultSetTable(ITableMetaData metaData, IDatabaseConnection connection) throws DataSetException, SQLException {
		statement = createStatement(connection);
        String escapePattern = (String)connection.getConfig().getProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN);
        try {
            String schema = connection.getSchema();
            String selectStatement = getSelectStatement(schema, metaData, escapePattern);
            resultSet = statement.executeQuery(selectStatement);
            this.metaData = metaData;
        } catch (SQLException e) {
            statement.close();
            statement = null;
            throw e;
        }
    }

    private Statement createStatement(IDatabaseConnection connection) throws SQLException {
        Connection jdbcConnection = connection.getConnection();
        Statement stmt = jdbcConnection.createStatement();
        connection.getConfig().getConfigurator().configureStatement(stmt);
        return stmt;
    }

    static String getSelectStatement(String schema, ITableMetaData metaData, String escapePattern) throws DataSetException {
        return DatabaseDataSet.getSelectStatement(schema, metaData, escapePattern);
    }

    public ITableMetaData getTableMetaData()
    {
        return metaData;
    }

    public void close() throws DataSetException {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }
        } catch (SQLException e) {
            throw new DataSetException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getClass().getName() + "[" + "metaData=[" + metaData + "], " + "resultSet=[" + resultSet + "], " + "statement=[" + statement + "]" + "]";
    }
}