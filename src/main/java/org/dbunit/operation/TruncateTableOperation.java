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
import org.dbunit.dataset.IDataSet;
import org.dbunit.util.DdlExecutor;

import java.sql.SQLException;

/**
 * Truncate tables present in the specified dataset. If the dataset does not
 * contains a particular table, but that table exists in the database,
 * the database table is not affected. Table are truncated in
 * reverse sequence.
 * <p>
 * This operation has the same effect of as {@link DeleteAllOperation}.
 * TruncateTableOperation is faster, and it is non-logged, meaning it cannot be
 * rollback. DeleteAllOperation is more portable because not all database vendor
 * support TRUNCATE_TABLE TABLE statement.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @see DeleteAllOperation
 * @since Apr 10, 2003
 */
@Slf4j
@NoArgsConstructor
public class TruncateTableOperation extends DeleteAllOperation {

    protected String getDeleteAllCommand() {
        return "truncate table ";
    }

    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        log.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        // Patch to make it work with MS SQL Server
        DatabaseConfig config = connection.getConfig();
        boolean oldValue = config.getFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS);
        try {
            config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, false);
            super.execute(connection, dataSet);
        } finally {
            config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, oldValue);
        }
    }

    public void execute(IDatabaseConnection connection, String table) throws Exception {
        log.debug("execute(connection={}, sql={}) - start", connection, table);
        DdlExecutor.executeSql(connection.getConnection(), "truncate table " + table);
    }
}