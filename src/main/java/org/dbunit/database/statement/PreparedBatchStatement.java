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

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
@Slf4j
public class PreparedBatchStatement extends AbstractPreparedBatchStatement {
    private int index;

    PreparedBatchStatement(String sql, Connection connection) throws SQLException {
        super(sql, connection);
        index = 0;
    }

    public void addValue(Object value, DataType dataType) throws TypeCastException, SQLException {
        log.debug("addValue(value={}, dataType={}) - start", value, dataType);

        // Special NULL handling
        if (value == null || value == ITable.NO_VALUE) {
            String sqlTypeName = dataType.getSqlTypeName();
            if (sqlTypeName == null) {
                statement.setNull(++index, dataType.getSqlType());
            } else {
                statement.setNull(++index, dataType.getSqlType(), sqlTypeName);
            }
            return;
        }

        dataType.setSqlValue(value, ++index, statement);
    }

    public void addBatch() throws SQLException {
        log.debug("addBatch() - start");
        statement.addBatch();
        index = 0;
    }

    public int executeBatch() throws SQLException {
        log.debug("executeBatch() - start");
        int[] results = statement.executeBatch();
        int result = 0;
        for (int j : results) {
            result += j;
        }
        return result;
    }

    public void clearBatch() throws SQLException {
        log.debug("clearBatch() - start");
        statement.clearBatch();
    }
}