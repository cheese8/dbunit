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
package org.dbunit.dataset;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 9, 2003
 */
@Slf4j
public class ForwardOnlyTable implements ITable {

    private final ITable table;
    private int lastRow = -1;

    public ForwardOnlyTable(ITable table) {
        this.table = table;
    }

    public ITableMetaData getTableMetaData() {
        return table.getTableMetaData();
    }

    public int getRowCount() {
        throw new UnsupportedOperationException();
    }

    public Object getValue(int row, String column) throws DataSetException {
        log.debug("getValue(row={}, columnName={}) - start", row, column);
        if (row < lastRow) {
            throw new UnsupportedOperationException("Cannot go backward!");
        }
        lastRow = row;
        return table.getValue(row, column);
    }
}