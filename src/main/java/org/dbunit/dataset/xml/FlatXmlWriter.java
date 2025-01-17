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
package org.dbunit.dataset.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.dataset.stream.DataSetProducerAdapter;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.util.xml.XmlWriter;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.5.5 (Apr 19, 2003)
 */
@Slf4j
public class FlatXmlWriter implements IDataSetConsumer {
    private static final String DATASET = "dataset";

    private final XmlWriter xmlWriter;
    private ITableMetaData activeMetaData;
    private int activeRowCount;
    @Setter
    private boolean includeEmptyTable = false;
    @Setter
    private String systemId = null;

    public FlatXmlWriter(OutputStream out, boolean xmlElement, boolean sortColumn, String[] replacements) throws IOException {
        this(out, null, xmlElement, sortColumn, replacements);
    }

    /**
     * @param outputStream The stream to which the XML will be written.
     * @param encoding     The encoding to be used for the {@link XmlWriter}.
     *                     Can be null. See {@link XmlWriter#XmlWriter(OutputStream, String, boolean, boolean, String[])}.
     */
    public FlatXmlWriter(OutputStream outputStream, String encoding, boolean xmlElement, boolean sortColumn, String[] replacements) throws UnsupportedEncodingException {
        xmlWriter = new XmlWriter(outputStream, encoding, xmlElement, sortColumn, replacements);
        xmlWriter.enablePrettyPrint(true);
    }

    public FlatXmlWriter(Writer writer, boolean xmlElement, boolean sortColumn, String[] replacements) {
        xmlWriter = new XmlWriter(writer, xmlElement, sortColumn, replacements);
        xmlWriter.enablePrettyPrint(true);
    }

    public FlatXmlWriter(Writer writer, String encoding, boolean xmlElement, boolean sortColumn, String[] replacements) {
        xmlWriter = new XmlWriter(writer, encoding, xmlElement, sortColumn, replacements);
        xmlWriter.enablePrettyPrint(true);
    }

    /**
     * Enable or disable pretty print of the XML.
     *
     * @param enabled <code>true</code> to enable pretty print (which is the default).
     *                <code>false</code> otherwise.
     * @since 2.4
     */
    public void setPrettyPrint(boolean enabled) {
        xmlWriter.enablePrettyPrint(enabled);
    }

    /**
     * Writes the given {@link IDataSet} using this writer.
     *
     * @param dataSet The {@link IDataSet} to be written
     */
    public void write(IDataSet dataSet) throws DataSetException {
        log.debug("write(dataSet={}) - start", dataSet);
        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
    }

    public void startDataSet() throws DataSetException {
        log.debug("startDataSet() - start");
        try {
            xmlWriter.writeDeclaration();
            xmlWriter.writeDoctype(systemId, null);
            xmlWriter.writeElement(DATASET);
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    public void endDataSet() throws DataSetException {
        log.debug("endDataSet() - start");
        try {
            xmlWriter.endElement();
            xmlWriter.close();
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    public void startTable(ITableMetaData metaData) throws DataSetException {
        log.debug("startTable(metaData={}) - start", metaData);
        activeMetaData = metaData;
        activeRowCount = 0;
    }

    public void endTable() throws DataSetException {
        log.debug("endTable() - start");
        if (includeEmptyTable && activeRowCount == 0) {
            try {
                String tableName = activeMetaData.getTableName();
                xmlWriter.writeEmptyElement(tableName);
            } catch (IOException e) {
                throw new DataSetException(e);
            }
        }
        activeMetaData = null;
    }

    public void row(Object[] values) throws DataSetException {
        log.debug("row(values={}) - start", values);
        try {
            String tableName = activeMetaData.getTableName();
            xmlWriter.writeElement(tableName);

            Column[] columns = activeMetaData.getColumns();
            if (xmlWriter.isSortColumn()) {
                Arrays.sort(columns);
            }
            for (int i = 0; i < columns.length; i++) {
                String columnName = columns[i].getColumnName();
                Object value = values[i];

                // Skip null value
                if (value == null) {
                    continue;
                }

                try {
                    String stringValue = DataType.asString(value);
                    String entryValue = xmlWriter.getReplacements().get(columnName);
                    if (xmlWriter.isXmlElememt()) {
                        xmlWriter.writeElementWithText(columnName, entryValue != null ? entryValue : stringValue);
                    } else {
                        xmlWriter.writeAttribute(columnName, entryValue != null ? entryValue : stringValue, true);
                    }
                } catch (TypeCastException e) {
                    throw new DataSetException("table=" + activeMetaData.getTableName() + ", row=" + i + ", column=" + columnName + ", value=" + value, e);
                }
            }

            activeRowCount++;
            xmlWriter.endElement();
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }
}