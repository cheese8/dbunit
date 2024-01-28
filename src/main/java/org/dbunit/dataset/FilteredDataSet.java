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

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.filter.SequenceTableFilter;

/**
 * Decorates a dataset and exposes only some tables from it. Can be used with
 * different filtering strategies.
 *
 * @author Manuel Laflamme
 * @author Last changed by: Luke Cann
 * @version $Revision$
 * @see ITableFilter
 * @see SequenceTableFilter
 * @see org.dbunit.dataset.filter.DefaultTableFilter
 * @since Feb 22, 2002
 */
@Slf4j
public class FilteredDataSet extends AbstractDataSet {

    private final IDataSet dataSet;
    private final ITableFilter filter;

    /**
     * Creates a FilteredDataSet that decorates the specified dataset and
     * exposes only the specified tables using {@link SequenceTableFilter} as
     * filtering strategy.
     *
     * @throws AmbiguousTableNameException If the given tableNames array contains ambiguous names
     */
    public FilteredDataSet(String[] tableNames, IDataSet dataSet) throws AmbiguousTableNameException {
        super(dataSet.isCaseSensitiveTableNames());
        filter = new SequenceTableFilter(tableNames, dataSet.isCaseSensitiveTableNames());
        this.dataSet = dataSet;
    }

    /**
     * Creates a FilteredDataSet that decorates the specified dataset and
     * exposes only the tables allowed by the specified filter.
     *
     * @param dataSet the filtered dataset
     * @param filter  the filtering strategy
     */
    public FilteredDataSet(ITableFilter filter, IDataSet dataSet) {
        super(dataSet.isCaseSensitiveTableNames());
        this.dataSet = dataSet;
        this.filter = filter;
    }

    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        log.debug("createIterator(reversed={}) - start", reversed);
        return filter.iterator(dataSet, reversed);
    }

    public String[] getTableNames() throws DataSetException {
        return filter.getTableNames(dataSet);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        if (!filter.accept(tableName)) {
            throw new NoSuchTableException(tableName);
        }
        return dataSet.getTableMetaData(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException {
        log.debug("getTable(tableName={}) - start", tableName);
        if (!filter.accept(tableName)) {
            throw new NoSuchTableException(tableName);
        }
        return dataSet.getTable(tableName);
    }
}