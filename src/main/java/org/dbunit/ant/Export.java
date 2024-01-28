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
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.Project;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlWriter;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * The <code>Export</code> class is the step that facilitates exporting
 * the contents of the database and/or it's corresponding DTD to a file.
 * The export can be performed on a full dataset or a partial one if
 * specific table names are identified.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @version $Revision$
 * @see DbUnitTaskStep
 * @since Jun 10, 2002
 */
@Slf4j
@NoArgsConstructor
public class Export extends AbstractStep {
    @Getter
    @Setter
    private File dest;
    @Getter
    @Setter
    private String format = FormatSupport.FLAT.getFormat();
    @Getter
    @Setter
    private String doctype = null;
    @Getter
    @Setter
    private String encoding = null; // if no encoding set by script than the default encoding (UTF-8) of the writer is used
    @Getter
    private final List<Object> tables = new ArrayList<>();

    private String getAbsolutePath(File filename) {
        return filename != null ? filename.getAbsolutePath() : "null";
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public void addQuery(Query query) {
        tables.add(query);
    }

    public void addQuerySet(QuerySet querySet) {
        tables.add(querySet);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException {
        try {
            if (dest == null) {
                throw new DatabaseUnitException("'dest' is a required attribute of the <export> step");
            }

            IDataSet dataset = getExportDataSet(connection);
            log("dataset tables: " + Arrays.asList(dataset.getTableNames()), Project.MSG_VERBOSE);

            // Write the dataset
            if (format.equals(FormatSupport.CSV.getFormat())) {
                CsvDataSetWriter.write(dataset, dest);
            } else {
                try (OutputStream out = Files.newOutputStream(dest.toPath())) {
                    if (format.equalsIgnoreCase(FormatSupport.FLAT.getFormat())) {
                        FlatXmlWriter writer = new FlatXmlWriter(out, getEncoding());
                        writer.setSystemId(doctype);
                        writer.write(dataset);
                    } else if (format.equalsIgnoreCase(FormatSupport.XML.getFormat())) {
                        XmlDataSet.write(dataset, out, getEncoding());
                    } else if (format.equalsIgnoreCase(FormatSupport.DTD.getFormat())) {
                        //TODO Should DTD also support encoding? It is basically an XML file...
                        FlatDtdDataSet.write(dataset, out);//, getEncoding());
                    } else if (format.equalsIgnoreCase(FormatSupport.XLS.getFormat())) {
                        XlsDataSet.write(dataset, out);
                    } else {
                        throw new IllegalArgumentException("The given format '" + format + "' is not supported");
                    }
                }
            }
            log("Successfully wrote file '" + dest + "'", Project.MSG_INFO);
        } catch (SQLException | IOException e) {
            throw new DatabaseUnitException(e);
        }
    }

    /**
     * Creates the dataset that is finally used for the export
     *
     * @return The final dataset used for the export
     */
    protected IDataSet getExportDataSet(IDatabaseConnection connection) throws DatabaseUnitException, SQLException {
        IDataSet dataset = getDatabaseDataSet(connection, tables);
        if (isOrdered()) {
            // Use topologically sorted database
            ITableFilter filter = new DatabaseSequenceFilter(connection);
            dataset = new FilteredDataSet(filter, dataset);
        }
        return dataset;
    }

    public String getLogMessage() {
        return "Executing export: in format: " + format + " to datafile: " + getAbsolutePath(dest);
    }

    public String toString() {
        return "Export: " + " dest=" + getAbsolutePath(dest) + ", format= " + format + ", doctype= " + doctype + ", tables= " + tables;
    }
}