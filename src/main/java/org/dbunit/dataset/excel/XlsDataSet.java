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
package org.dbunit.dataset.excel;

import java.io.*;

import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import org.apache.poi.ss.usermodel.*;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.OrderedTableNameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pjfanning.xlsx.StreamingReader;

/**
 * This dataset implementation can read and write MS Excel documents. Each
 * sheet represents a table. The first row of a sheet defines the columns names
 * and remaining rows contains the data.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 21, 2003
 */
public class XlsDataSet extends AbstractDataSet {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(XlsDataSet.class);

    private final OrderedTableNameMap _tables;

    /**
     * Creates a new XlsDataSet object that loads the specified Excel document.
     */
    public XlsDataSet(File file) throws IOException, DataSetException {
        _tables = super.createTableNameMap();
        Workbook workbook = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(inputStream);
        }catch(Exception ex) {
            try {
                workbook = StreamingReader.builder()
                        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                        .bufferSize(4096)     // buffer size (in bytes) to use when reading InputStream to file (defaults to 1024)
                        .setSharedStringsImplementationType(SharedStringsImplementationType.POI_READ_ONLY)
                        .open(file);
            }catch(Exception e) {
            }
        }
        if (workbook == null) {
            throw new IOException();
        }

        for (Sheet sheet : workbook) {
            ITable table = new XlsTable(sheet.getSheetName(), sheet);
            _tables.add(table.getTableMetaData().getTableName(), table);
        }
    }

    /**
     * Creates a new XlsDataSet object that loads the specified Excel document.
     */
    public XlsDataSet(InputStream in) throws IOException, DataSetException {
        _tables = super.createTableNameMap();

        Workbook workbook = WorkbookFactory.create(in);

        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
            ITable table = new XlsTable(workbook.getSheetName(i),
                    workbook.getSheetAt(i));
            _tables.add(table.getTableMetaData().getTableName(), table);
        }
    }

    /**
     * Write the specified dataset to the specified Excel document.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException {
        logger.debug("write(dataSet={}, out={}) - start", dataSet, out);

        new XlsDataSetWriter().write(dataSet, out);
    }


    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException {
        if (logger.isDebugEnabled())
            logger.debug("createIterator(reversed={}) - start", String.valueOf(reversed));

        ITable[] tables = (ITable[]) _tables.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }
}
