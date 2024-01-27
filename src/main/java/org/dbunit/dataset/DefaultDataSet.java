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

/**
 * Simple implementation of a dataset backed by {@link ITable} objects which can
 * be added dynamically.
 * 
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Feb 18, 2002)
 */
@Slf4j
public class DefaultDataSet extends AbstractDataSet {

    public DefaultDataSet() {
    	super();
    }

    /**
     * Creates a default dataset which is empty initially
     * @since 2.4.2
     */
    public DefaultDataSet(boolean caseSensitiveTableNames)
    {
        super(caseSensitiveTableNames);
    }

    public DefaultDataSet(ITable table) throws AmbiguousTableNameException {
        this(new ITable[]{table});
    }

    public DefaultDataSet(ITable table1, ITable table2) throws AmbiguousTableNameException {
        this(new ITable[] {table1, table2});
    }

    public DefaultDataSet(ITable[] tables) throws AmbiguousTableNameException {
        this(tables, false);
    }
    
    /**
     * Creates a default dataset which consists of the given tables
     * @since 2.4.2
     */
    public DefaultDataSet(ITable[] tables, boolean caseSensitiveTableNames) throws AmbiguousTableNameException {
        super(caseSensitiveTableNames);
        for (ITable table : tables) {
            addTable(table);
        }
    }

    /**
     * Add a new table in this dataset.
     */
    public void addTable(ITable table) throws AmbiguousTableNameException {
        log.debug("addTable(table={}) - start", table);
        this.initialize();
        super.orderedTableNameMap.add(table.getTableMetaData().getTableName(), table);
    }

    /**
     * Initializes the {@link AbstractDataSet#orderedTableNameMap} of the parent class if it is not initialized yet.
     * @since 2.4.6
     */
    protected void initialize() {
        log.debug("initialize() - start");
        if(orderedTableNameMap != null) {
            log.debug("The table name map has already been initialized.");
            return;
        }
        // Gather all tables in the OrderedTableNameMap which also makes the duplicate check
        orderedTableNameMap = this.createTableNameMap();
    }

    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        log.debug("createIterator(reversed={}) - start", reversed);
        this.initialize();
        ITable[] tables = orderedTableNameMap.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }
}