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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tools.ant.ProjectComponent;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ForwardOnlyDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvProducer;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatDtdProducer;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlProducer;
import org.dbunit.util.FileHelper;
import org.xml.sax.InputSource;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.1 (Apr 3, 2004)
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractStep extends ProjectComponent implements DbUnitTaskStep {
    private boolean ordered = false;

    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection, List<Object> tables) throws DatabaseUnitException {
        log.debug("getDatabaseDataSet(connection={}, tables={}) - start", connection, tables);
        try {
            DatabaseConfig config = connection.getConfig();
            if (CollectionUtils.isEmpty(tables)) {
                log.debug("Retrieving the whole database because tables or queries have not been specified");
                return connection.createDataSet();
            }
            return getCompositeDataSet(config, createQueryDataSet(tables, connection));
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        }
    }

    private CompositeDataSet getCompositeDataSet(DatabaseConfig config, List<QueryDataSet> queryDataSets) throws DataSetException {
        String clazzName = "org.dbunit.database.ForwardOnlyResultSetTableFactory";
        if (clazzName.equals(config.getProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY).getClass().getName())) {
            IDataSet[] dataSetsArray = createForwardOnlyDataSetArray(queryDataSets);
            return new CompositeDataSet(dataSetsArray);
        }
        IDataSet[] dataSetsArray = queryDataSets.toArray(new IDataSet[0]);
        return new CompositeDataSet(dataSetsArray);
    }

    private ForwardOnlyDataSet[] createForwardOnlyDataSetArray(List<QueryDataSet> dataSets) {
        ForwardOnlyDataSet[] forwardOnlyDataSets = new ForwardOnlyDataSet[dataSets.size()];
        for (int i = 0; i < dataSets.size(); i++) {
            forwardOnlyDataSets[i] = new ForwardOnlyDataSet(dataSets.get(i));
        }
        return forwardOnlyDataSets;
    }

    private List<QueryDataSet> createQueryDataSet(List<Object> tables, IDatabaseConnection connection) throws DataSetException {
        log.debug("createQueryDataSet(tables={}, connection={})", tables, connection);
        List<QueryDataSet> queryDataSets = new ArrayList<>();
        QueryDataSet queryDataSet = new QueryDataSet(connection);
        for (Object item : tables) {
            if (item instanceof QuerySet) {
                QuerySet querySetItem = (QuerySet) item;
                addQueryDataSet(queryDataSets, queryDataSet);
                queryDataSets.add(querySetItem.getQueryDataSet(connection));
                queryDataSet = new QueryDataSet(connection);
            } else if (item instanceof Query) {
                Query queryItem = (Query) item;
                queryDataSet.addTable(queryItem.getName(), queryItem.getSql());
            } else if (item instanceof Table) {
                Table tableItem = (Table) item;
                queryDataSet.addTable(tableItem.getName());
            } else {
                throw new IllegalArgumentException("Unsupported element type " + item.getClass().getName());
            }
        }
        addQueryDataSet(queryDataSets, queryDataSet);
        return queryDataSets;
    }

    private void addQueryDataSet(List<QueryDataSet> queryDataSets, QueryDataSet queryDataSet) throws DataSetException {
        if (queryDataSet.getTableNames().length > 0) {
            queryDataSets.add(queryDataSet);
        }
    }

    protected IDataSet getSrcDataSet(File src, String format, boolean forwardOnly) throws DatabaseUnitException {
        log.debug("getSrcDataSet(src={}, format={}, forwardOnly={}) - start", src, format, forwardOnly);
        try {
            IDataSetProducer producer;
            if (format.equalsIgnoreCase(FormatSupport.XML.getFormat())) {
                producer = new XmlProducer(getInputSource(src));
            } else if (format.equalsIgnoreCase(FormatSupport.CSV.getFormat())) {
                producer = new CsvProducer(src);
            } else if (format.equalsIgnoreCase(FormatSupport.FLAT.getFormat())) {
                producer = new FlatXmlProducer(getInputSource(src), true, true);
            } else if (format.equalsIgnoreCase(FormatSupport.DTD.getFormat())) {
                producer = new FlatDtdProducer(getInputSource(src));
            } else if (format.equalsIgnoreCase(FormatSupport.XLS.getFormat())) {
                return new CachedDataSet(new XlsDataSet(src));
            } else {
                throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml', 'csv', 'xls' or 'dtd' but was: " + format);
            }
            if (forwardOnly) {
                return new StreamingDataSet(producer);
            }
            return new CachedDataSet(producer);
        } catch (IOException e) {
            throw new DatabaseUnitException(e);
        }
    }

    /**
     * Creates and returns an {@link InputSource}
     *
     * @param file The file for which an {@link InputSource} should be created
     * @return The input source for the given file
     */
    public static InputSource getInputSource(File file) throws MalformedURLException {
        return FileHelper.createInputSource(file);
    }

    public String toString() {
        return "AbstractStep: ordered=" + ordered;
    }
}