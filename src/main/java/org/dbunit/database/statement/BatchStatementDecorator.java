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

import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
@Slf4j
public class BatchStatementDecorator implements IPreparedBatchStatement {
    private final IBatchStatement statement;
    private final String[] sqlTemplate;
    private StringBuffer sqlBuffer;
    private int index;

    BatchStatementDecorator(String sql, IBatchStatement statement) {
        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(sql, "?");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        if (sql.endsWith("?")) {
            list.add("");
        }
        sqlTemplate = list.toArray(new String[0]);
        this.statement = statement;
        // reset sql buffer
        index = 0;
        sqlBuffer = new StringBuffer(sqlTemplate[index++]);
    }

    public void addValue(Object value, DataType dataType) throws TypeCastException {
        log.debug("addValue(value={}, dataType={}) - start", value, dataType);
        sqlBuffer.append(DataSetUtils.getSqlValueString(value, dataType));
        sqlBuffer.append(sqlTemplate[index++]);
    }

    public void addBatch() throws SQLException {
        log.debug("addBatch() - start");
        statement.addBatch(sqlBuffer.toString());
        // reset sql buffer
        index = 0;
        sqlBuffer = new StringBuffer(sqlTemplate[index++]);
    }

    public int executeBatch() throws SQLException {
        log.debug("executeBatch() - start");
        return statement.executeBatch();
    }

    public void clearBatch() throws SQLException {
        log.debug("clearBatch() - start");
        statement.clearBatch();
        // reset sql buffer
        index = 0;
        sqlBuffer = new StringBuffer(sqlTemplate[index++]);
    }

    public void close() throws SQLException {
        log.debug("close() - start");
        statement.close();
    }
}