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

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Jun 12, 2003
 */
@Slf4j
public class AutomaticPreparedBatchStatement implements IPreparedBatchStatement {
    private final IPreparedBatchStatement statement;
    private int batchCount = 0;
    private final int threshold;
    private int result = 0;

    public AutomaticPreparedBatchStatement(IPreparedBatchStatement statement, int threshold) {
        this.statement = statement;
        this.threshold = threshold;
    }

    public void addValue(Object value, DataType dataType) throws TypeCastException, SQLException {
        log.debug("addValue(value={}, dataType={}) - start", value, dataType);
        statement.addValue(value, dataType);
    }

    public void addBatch() throws SQLException {
        log.debug("addBatch() - start");
        statement.addBatch();
        batchCount++;
        if (batchCount % threshold == 0) {
            result += statement.executeBatch();
        }
    }

    public int executeBatch() throws SQLException {
        log.debug("executeBatch() - start");
        result += statement.executeBatch();
        return result;
    }

    public void clearBatch() throws SQLException {
        log.debug("clearBatch() - start");
        statement.clearBatch();
        batchCount = 0;
    }

    public void close() throws SQLException {
        log.debug("close() - start");
        statement.close();
    }
}