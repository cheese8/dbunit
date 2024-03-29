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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Decorates an operation and executes within the context of a transaction.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 21, 2002
 */
@Slf4j
@AllArgsConstructor
public class TransactionOperation extends DatabaseOperation {

    private final DatabaseOperation operation;

    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        log.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        Connection jdbcConnection = connection.getConnection();

        if (!jdbcConnection.getAutoCommit()) {
            throw new ExclusiveTransactionException();
        }

        jdbcConnection.setAutoCommit(false);
        try {
            operation.execute(connection, dataSet);
            jdbcConnection.commit();
        } catch (DatabaseUnitException | SQLException | RuntimeException e) {
            jdbcConnection.rollback();
            throw e;
        } finally {
            jdbcConnection.setAutoCommit(true);
        }
    }
}