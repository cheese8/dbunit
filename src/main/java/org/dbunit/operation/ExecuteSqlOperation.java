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
package org.dbunit.operation;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

import java.io.File;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Deletes all rows of tables present in the specified dataset. If the dataset
 * does not contains a particular table, but that table exists in the database,
 * the database table is not affected. Table are truncated in
 * reverse sequence.
 * <p/>
 * This operation has the same effect of as {@link TruncateTableOperation}.
 * TruncateTableOperation is faster, and it is non-logged, meaning it cannot be
 * rollback. DeleteAllOperation is more portable because not all database vendor
 * support TRUNCATE_TABLE TABLE statement.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @see ExecuteSqlOperation
 * @since Feb 18, 2002
 */
@Slf4j
@NoArgsConstructor
public class ExecuteSqlOperation extends AbstractOperation {

    @Override
    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws DatabaseUnitException, SQLException {

    }

    public void execute(IDatabaseConnection connection, File dataSet) throws DatabaseUnitException, SQLException {
        //log.debug("execute(connection={}, dataSet={}, scripts={}) - start", connection, dataSet, scripts);

        DatabaseConfig databaseConfig = connection.getConfig();
        IStatementFactory statementFactory = (IStatementFactory) databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
        IBatchStatement statement = statementFactory.createBatchStatement(connection);
        try {
            // delete tables once each in reverse order of seeing them.
            //for(String each : scripts) {
            //    String sql = each;
            //    statement.addBatch(sql);
            //    log.debug("Added SQL: {}", sql);
            //}
            statement.executeBatch();
            statement.clearBatch();
        } finally {
            statement.close();
        }
    }

    public void execute(IDatabaseConnection connection, String dataSet) throws DatabaseUnitException, SQLException {
        //log.debug("execute(connection={}, dataSet={}, scripts={}) - start", connection, dataSet, scripts);

        DatabaseConfig databaseConfig = connection.getConfig();
        IStatementFactory statementFactory = (IStatementFactory) databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
        IBatchStatement statement = statementFactory.createBatchStatement(connection);
        try {
            // delete tables once each in reverse order of seeing them.
            //for(String each : scripts) {
            //    String sql = each;
            //    statement.addBatch(sql);
            //    log.debug("Added SQL: {}", sql);
            //}
            statement.executeBatch();
            statement.clearBatch();
        } finally {
            statement.close();
        }
    }
}