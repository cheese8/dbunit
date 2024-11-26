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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator that replaces configured values from the decorated table
 * with replacement values.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 17, 2003
 */
public class ReplacementTable implements ITable {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ReplacementTable.class);

    private final ITable _table;

    /**
     * Create a new ReplacementTable object that decorates the specified table.
     *
     * @param table the decorated table
     */
    public ReplacementTable(ITable table) {
        _table = table;
    }

    ////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData() {
        return _table.getTableMetaData();
    }

    public int getRowCount() {
        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException {
        if (logger.isDebugEnabled())
            logger.debug("getValue(row={}, columnName={}) - start", row, column);

        Object value = _table.getValue(row, column);
        return Replacements.getValue(value);
    }

    public String toString() {
        return getClass().getName() + "[" +
                "_table=" + _table +
                "]";
    }
}