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
package org.dbunit.ant;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.filter.DefaultColumnFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Compare</code> class is the step that compare the content of the
 * database against the specified dataset.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 3, 2004
 * @see DbUnitTaskStep
 */
@Slf4j
public class Compare extends AbstractStep {
    @Getter @Setter
    private String format = FormatSupport.FLAT.getFormat();
    @Getter @Setter
    private File src;
    @Getter
    private final List<Object> tables = new ArrayList<>();
    @Setter
    private boolean sort = false;

    public void addTable(Table table) {
        log.debug("addTable(table={}) - start", table);
        tables.add(table);
    }

    public void addQuery(Query query) {
        log.debug("addQuery(query={}) - start", query);
        tables.add(query);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException {
        log.debug("execute(connection={}) - start", connection);
        IDataSet expectedDataset = getSrcDataSet(src, getFormat(), false);
        IDataSet actualDataset = getDatabaseDataSet(connection, tables);

        String[] tableNames;
        if (tables.size() == 0) {
            // No tables specified, assume must compare all tables from expected dataset
            tableNames = expectedDataset.getTableNames();
        } else {
            tableNames = actualDataset.getTableNames();
        }

        for (String tableName : tableNames) {
            ITable expectedTable;
            try {
                expectedTable = expectedDataset.getTable(tableName);
            } catch (NoSuchTableException e) {
                throw new DatabaseUnitException("Did not find table in source file '" + src + "' using format '" + getFormat() + "'", e);
            }
            ITableMetaData expectedMetaData = expectedTable.getTableMetaData();

            ITable actualTable;
            try {
                actualTable = actualDataset.getTable(tableName);
            } catch (NoSuchTableException e) {
                throw new DatabaseUnitException("Did not find table in actual dataset '" + actualDataset + "' via db connection '" + connection + "'", e);
            }
            // Only compare columns present in expected table. Extra columns are filtered out from actual database table.
            actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedMetaData.getColumns());

            if (sort) {
                expectedTable = new SortedTable(expectedTable);
                actualTable = new SortedTable(actualTable);
            }
            Assertion.assertEquals(expectedTable, actualTable);
        }
    }

    public String getLogMessage() {
        return "Executing compare: from file: " + ((src == null) ? null : src.getAbsolutePath()) + " with format: " + format;
    }

    public String toString() {
        return "Compare: src=" + (src == null ? "null" : src.getAbsolutePath()) + ", format= " + format + ", tables= " + tables;
    }
}