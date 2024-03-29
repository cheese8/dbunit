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

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public abstract class AbstractTable implements ITable {

    protected void assertValidRowIndex(int row) throws DataSetException {
        assertValidRowIndex(row, getRowCount());
    }

    protected void assertValidRowIndex(int row, int rowCount) throws DataSetException {
        if (row < 0) {
            throw new RowOutOfBoundsException(row + " < 0");
        }

        if (row >= rowCount) {
            throw new RowOutOfBoundsException(row + " >= " + rowCount);
        }
    }

    protected void assertValidColumn(String columnName) throws DataSetException {
        ITableMetaData metaData = getTableMetaData();
        // Try to find the column in the metadata - if it cannot be found an exception is thrown
        Columns.getColumnValidated(columnName, metaData.getColumns(), metaData.getTableName());
    }

    protected int getColumnIndex(String columnName) throws DataSetException {
        ITableMetaData metaData = getTableMetaData();
        return metaData.getColumnIndex(columnName);
    }
}