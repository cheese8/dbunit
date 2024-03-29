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
import org.dbunit.database.QueryTableIterator;

/**
 * Decorator that allows forward only access to decorated dataset.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 9, 2003
 */
@Slf4j
public class ForwardOnlyDataSet extends AbstractDataSet {
    private final IDataSet dataSet;
    private int iteratorCount;

    public ForwardOnlyDataSet(IDataSet dataSet) {
        this.dataSet = dataSet;
    }

    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        log.debug("createIterator(reversed={}) - start", reversed);
        if (reversed) {
            throw new UnsupportedOperationException("Reverse iterator not supported!");
        }
        if (iteratorCount > 0) {
            throw new UnsupportedOperationException("Only one iterator allowed!");
        }
        return new ForwardOnlyIterator(dataSet.iterator());
    }

    public String[] getTableNames() throws DataSetException {
        throw new UnsupportedOperationException();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        throw new UnsupportedOperationException();
    }

    public ITable getTable(String tableName) throws DataSetException {
        throw new UnsupportedOperationException();
    }

    private class ForwardOnlyIterator implements ITableIterator {
        private final ITableIterator iterator;

        public ForwardOnlyIterator(ITableIterator iterator) {
            this.iterator = iterator;
            iteratorCount++;
        }

        public boolean next() throws DataSetException {
            if (iterator instanceof QueryTableIterator) {
                return ((QueryTableIterator) iterator).nextWithoutClosing();
            }
            return iterator.next();
        }

        public ITableMetaData getTableMetaData() throws DataSetException {
            return iterator.getTableMetaData();
        }

        public ITable getTable() throws DataSetException {
            return new ForwardOnlyTable(iterator.getTable());
        }
    }
}