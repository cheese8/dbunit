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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 15, 2002
 */
@Slf4j
public abstract class AbstractBatchStatement implements IBatchStatement {
    protected final Statement statement;

    AbstractBatchStatement(Connection connection) throws SQLException {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            log.error("getConnection(): ", e);
            throw e;
        }
    }

    public void close() throws SQLException {
        log.debug("close() - start");
        statement.close();
    }
}