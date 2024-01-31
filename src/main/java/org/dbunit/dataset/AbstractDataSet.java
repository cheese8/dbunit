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

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This abstract class provides the basic implementation of the IDataSet
 * interface. Subclass are only required to implement the {@link #createIterator}
 * method.
 *
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Feb 22, 2002)
 */
@Slf4j
@NoArgsConstructor
public abstract class AbstractDataSet implements IDataSet {
    //TODO (matthias) Use a DataSetBuilder PLUS IDataSet to avoid this ugly lazy initialization with loads of protected internals a user must know...

    protected OrderedTableNameMap orderedTableNameMap;

    /**
     * Whether table names of this dataset are case-sensitive.
     * By default, case-sensitivity is set to false for datasets
     */
    private boolean caseSensitiveTableNames = false;

    /**
     * Constructor
     *
     * @param caseSensitiveTableNames Whether table names should be case-sensitive
     * @since 2.4
     */
    public AbstractDataSet(boolean caseSensitiveTableNames) {
        this.caseSensitiveTableNames = caseSensitiveTableNames;
    }

    /**
     * @return <code>true</code> if the case sensitivity of table names is used in this dataset.
     * @since 2.4
     */
    public boolean isCaseSensitiveTableNames() {
        return caseSensitiveTableNames;
    }

    /**
     * Creates and returns a new instance of the table names container.
     * Implementors should use this method to retrieve a map which stores
     * table names which can be linked with arbitrary objects.
     *
     * @return a new empty instance of the table names container
     * @since 2.4
     */
    protected OrderedTableNameMap createTableNameMap() {
        return new OrderedTableNameMap(caseSensitiveTableNames);
    }

    /**
     * Initializes the tables of this dataset
     *
     * @since 2.4
     */
    protected void initialize() throws DataSetException {
        if (orderedTableNameMap != null) {
            log.debug("The table name map has already been initialized.");
            return;
        }

        // Gather all tables in the OrderedTableNameMap which also makes the duplicate check
        orderedTableNameMap = this.createTableNameMap();
        ITableIterator iterator = createIterator(false);
        while (iterator.next()) {
            ITable table = iterator.getTable();
            orderedTableNameMap.add(table.getTableMetaData().getTableName(), table);
        }
    }

    /**
     * Creates an iterator which provides access to all tables of this dataset
     *
     * @param reversed Whether the created iterator should be a reversed one or not
     * @return The created {@link ITableIterator}
     */
    protected abstract ITableIterator createIterator(boolean reversed) throws DataSetException;

    public String[] getTableNames() throws DataSetException {
        initialize();
        return orderedTableNameMap.getTableNames();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        return getTable(tableName).getTableMetaData();
    }

    public ITable getTable(String tableName) throws DataSetException {
        initialize();
        ITable found = (ITable) orderedTableNameMap.get(tableName);
        if (found != null) {
            return found;
        }
        throw new NoSuchTableException(tableName);
    }

    public ITable[] getTables() throws DataSetException {
        initialize();
        return orderedTableNameMap.orderedValues().toArray(new ITable[0]);
    }

    public ITableIterator iterator() throws DataSetException {
        return createIterator(false);
    }

    public ITableIterator reverseIterator() throws DataSetException {
        return createIterator(true);
    }

    public String toString() {
        return "AbstractDataSet[" + "orderedTableNameMap=" + orderedTableNameMap + "]";
    }
}