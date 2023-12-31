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
import java.util.Iterator;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
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

    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection, List tables) throws DatabaseUnitException {
        log.debug("getDatabaseDataSet(connection={}, tables={}) - start", new Object[] { connection, tables});

        try {
            DatabaseConfig config = connection.getConfig();

            // Retrieve the complete database if no tables or queries specified.
            if (tables.size() == 0) {
            	log.debug("Retrieving the whole database because tables/queries have not been specified");
                return connection.createDataSet();
            }

            List queryDataSets = createQueryDataSet(tables, connection);

            IDataSet[] dataSetsArray;
            if (config.getProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY)
                    .getClass().getName().equals("org.dbunit.database.ForwardOnlyResultSetTableFactory")) {
                dataSetsArray = createForwardOnlyDataSetArray(queryDataSets);
            } else {
                dataSetsArray = (IDataSet[]) queryDataSets.toArray(new IDataSet[queryDataSets.size()]);
            }
            return new CompositeDataSet(dataSetsArray);
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        }
    }

    private ForwardOnlyDataSet[] createForwardOnlyDataSetArray(List<QueryDataSet> dataSets) throws SQLException {
        ForwardOnlyDataSet[] forwardOnlyDataSets = new ForwardOnlyDataSet[dataSets.size()];
        for (int i = 0; i < dataSets.size(); i++) {
            forwardOnlyDataSets[i] = new ForwardOnlyDataSet(dataSets.get(i));
        }
        return forwardOnlyDataSets;
    }
   
	private List createQueryDataSet(List tables, IDatabaseConnection connection) throws DataSetException, SQLException {
		log.debug("createQueryDataSet(tables={}, connection={})", tables, connection);
		
		List queryDataSets = new ArrayList();
		
        QueryDataSet queryDataSet = new QueryDataSet(connection);
        
        for (Iterator it = tables.iterator(); it.hasNext();) {
            Object item = it.next();
            
            if(item instanceof QuerySet) {
				if(queryDataSet.getTableNames().length > 0) {
                    queryDataSets.add(queryDataSet);
                }
				
				QueryDataSet newQueryDataSet = (((QuerySet)item).getQueryDataSet(connection));
				queryDataSets.add(newQueryDataSet);
				queryDataSet = new QueryDataSet(connection);
            } else if (item instanceof Query) {
                Query queryItem = (Query)item;
                queryDataSet.addTable(queryItem.getName(), queryItem.getSql());
            } else if (item instanceof Table) {
                Table tableItem = (Table)item;
                queryDataSet.addTable(tableItem.getName());
            } else {
            	throw new IllegalArgumentException("Unsupported element type " + item.getClass().getName() + ".");
            }
        }
        
        if(queryDataSet.getTableNames().length > 0) {
            queryDataSets.add(queryDataSet);
        }
        return queryDataSets;
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
	 * @param file The file for which an {@link InputSource} should be created
	 * @return The input source for the given file
	 * @throws MalformedURLException
	 */
	public static InputSource getInputSource(File file) throws MalformedURLException {
        return FileHelper.createInputSource(file);
	}

    public String toString() {
        return "AbstractStep: ordered=" + ordered;
    }
}