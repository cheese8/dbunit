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

package org.dbunit.database.statement;

import lombok.extern.slf4j.Slf4j;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 20, 2002
 */
@Slf4j
public class PreparedStatementFactory extends AbstractStatementFactory {
    public IBatchStatement createBatchStatement(IDatabaseConnection connection) throws SQLException {
        log.debug("createBatchStatement(connection={}) - start", connection);
        if (supportBatchStatement(connection)) {
            return new BatchStatement(connection.getConnection());
        } else {
            return new SimpleStatement(connection.getConnection());
        }
    }

    public IPreparedBatchStatement createPreparedBatchStatement(String sql, IDatabaseConnection connection) throws SQLException {
        log.debug("createPreparedBatchStatement(sql={}, connection={}) - start", sql, connection);
        Integer batchSize = (Integer) connection.getConfig().getProperty(DatabaseConfig.PROPERTY_BATCH_SIZE);
        IPreparedBatchStatement statement;
        if (supportBatchStatement(connection)) {
            statement = new PreparedBatchStatement(sql, connection.getConnection());
        } else {
            statement = new SimplePreparedStatement(sql, connection.getConnection());
        }
        return new AutomaticPreparedBatchStatement(statement, batchSize);
    }
}