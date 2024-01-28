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
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

/**
 * Hold copy of another dataset or a consumed provider content.
 *
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.x (Apr 18, 2003)
 */
@Slf4j
public class CachedDataSet extends AbstractDataSet implements IDataSetConsumer {
    private DefaultTable activeTable;

    /**
     * Default constructor.
     */
    public CachedDataSet() throws DataSetException {
        super();
        initialize();
    }

    /**
     * Creates a copy of the specified dataset.
     */
    public CachedDataSet(IDataSet dataSet) throws DataSetException {
        super(dataSet.isCaseSensitiveTableNames());
        initialize();

        final ITableIterator iterator = dataSet.iterator();
        while (iterator.next()) {
            final ITable table = iterator.getTable();
            orderedTableNameMap.add(table.getTableMetaData().getTableName(), new CachedTable(table));
        }
    }

    /**
     * Creates a CachedDataSet that synchronously consume the specified producer.
     */
    public CachedDataSet(IDataSetProducer producer) throws DataSetException {
        this(producer, false);
    }

    /**
     * Creates a CachedDataSet that synchronously consume the specified producer.
     * @param caseSensitiveTableNames Whether case-sensitive table names should be used
     */
    public CachedDataSet(IDataSetProducer producer, boolean caseSensitiveTableNames) throws DataSetException {
        super(caseSensitiveTableNames);
        initialize();

        producer.setConsumer(this);
        producer.produce();
    }

    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        log.debug("createIterator(reversed={}) - start", reversed);
        ITable[] tables = orderedTableNameMap.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }

    public void startDataSet() throws DataSetException {
        log.debug("startDataSet() - start");
        orderedTableNameMap = super.createTableNameMap();
    }

    public void endDataSet() throws DataSetException {
        log.debug("endDataSet() - start");
        log.debug("endDataSet() - the final tableMap is: " + orderedTableNameMap);
    }

    public void startTable(ITableMetaData metaData) throws DataSetException {
        log.debug("startTable(metaData={}) - start", metaData);
        activeTable = new DefaultTable(metaData);
    }

    public void endTable() throws DataSetException {
        log.debug("endTable() - start");
        String tableName = activeTable.getTableMetaData().getTableName();
        // Check whether the table appeared once before
        if(orderedTableNameMap.containsTable(tableName)) {
            DefaultTable existingTable = (DefaultTable)orderedTableNameMap.get(tableName);
            // Add all newly collected rows to the existing table
            existingTable.addTableRows(activeTable);
        } else {
            orderedTableNameMap.add(tableName, activeTable);
        }
        activeTable = null;
    }

    public void row(Object[] values) throws DataSetException {
        log.debug("row(values={}) - start", values);
        activeTable.addRow(values);
    }
}