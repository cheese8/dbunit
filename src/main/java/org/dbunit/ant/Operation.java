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
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The <code>Operation</code> class is the step that defines which
 * operation will be performed in the execution of the <code>DbUnitTask</code>
 * task.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @version $Revision$
 * @since Jun 10, 2002
 * @see org.dbunit.ant.DbUnitTaskStep
 */
@Slf4j
public class Operation extends AbstractStep {
    @Getter
    protected String type = "CLEAN_INSERT";
    @Getter @Setter
    private String format = FormatSupport.FLAT.getFormat();
    private final List<File> sources = new ArrayList<>();
    @Getter @Setter
    private boolean combine = false;
    @Getter @Setter
    private boolean transaction = false;
    @Getter
    private DatabaseOperation databaseOperation;
    private boolean forwardOperation = true;
    @Getter @Setter
    private String nullToken;

    @Setter @Getter
    private FileSet fileset;

    public File[] getSrc() {
        return sources.toArray(new File[0]);
    }

    public void setSrc(File[] sources) {
        this.sources.clear();
        this.sources.addAll(Arrays.asList(sources));
    }

    public void setSrc(File src) {
        sources.clear();
        String path = src.getAbsolutePath();
        path = path.replaceAll("/src/test/src/test", "/src/test");
        File newSrc = new File(path);
        sources.add(newSrc);
    }

    public void addConfiguredFileset(FileSet fileSet) {
        DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
        for (String file : scanner.getIncludedFiles()) {
            sources.add(new File(scanner.getBasedir(), file));
        }
    }

    public void setType(String type) {
        log.debug("setType(type={}) - start", type);
        if ("UPDATE".equals(type)) {
            databaseOperation = DatabaseOperation.UPDATE;
            forwardOperation = true;
        } else if ("INSERT".equals(type)) {
            databaseOperation = DatabaseOperation.INSERT;
            forwardOperation = true;
        } else if ("REFRESH".equals(type)) {
            databaseOperation = DatabaseOperation.REFRESH;
            forwardOperation = true;
        } else if ("DELETE".equals(type)) {
            databaseOperation = DatabaseOperation.DELETE;
            forwardOperation = false;
        } else if ("DELETE_ALL".equals(type)) {
            databaseOperation = DatabaseOperation.DELETE_ALL;
            forwardOperation = false;
        } else if ("CLEAN_INSERT".equals(type)) {
            databaseOperation = DatabaseOperation.CLEAN_INSERT;
            forwardOperation = false;
        } else if ("NONE".equals(type)) {
            databaseOperation = DatabaseOperation.NONE;
            forwardOperation = true;
        } else if ("MSSQL_CLEAN_INSERT".equals(type)) {
            databaseOperation = InsertIdentityOperation.CLEAN_INSERT;
            forwardOperation = false;
        } else if ("MSSQL_INSERT".equals(type)) {
            databaseOperation = InsertIdentityOperation.INSERT;
            forwardOperation = true;
        } else if ("MSSQL_REFRESH".equals(type)) {
            databaseOperation = InsertIdentityOperation.REFRESH;
            forwardOperation = true;
        } else {
            throw new IllegalArgumentException("Type must be one of: UPDATE, INSERT,"
                    + " REFRESH, DELETE, DELETE_ALL, CLEAN_INSERT, MSSQL_INSERT, "
                    + " or MSSQL_REFRESH but was: " + type);
        }
        this.type = type;
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException {
        log.debug("execute(connection={}) - start", connection);
        if (databaseOperation == null) {
            throw new DatabaseUnitException("Operation.execute(): setType(String) must be called before execute()!");
        }
        if (databaseOperation == DatabaseOperation.NONE) {
            return;
        }
        if (sources.size() == 0) {
            throw new DatabaseUnitException("Operation.execute(): must call setSrc(File), addSrc(File), or setSources(File[]) before execute()!");
        }

        try {
            DatabaseOperation operation = (transaction ? new TransactionOperation(this.databaseOperation) : this.databaseOperation);
            // TODO This is not very nice and the design should be reviewed but it works for now (gommma)
            boolean useForwardOnly = forwardOperation && ! isOrdered();
            IDataSet dataset;
            if (sources.size() > 1) {
                IDataSet[] datasets = new IDataSet[sources.size()];
                for (int i = 0; i < sources.size(); i++) {
                    datasets[i] = getSrcDataSet(sources.get(i), getFormat(), useForwardOnly);
                }
                dataset = new CompositeDataSet(datasets, combine);
            } else {
                dataset = getSrcDataSet(sources.get(0), getFormat(), useForwardOnly);
            }
            if (nullToken != null) {
                dataset = new ReplacementDataSet(dataset);
                ((ReplacementDataSet)dataset).addReplacementObject(nullToken, null);
            }
            if (isOrdered()) {
                DatabaseSequenceFilter databaseSequenceFilter = new DatabaseSequenceFilter(connection);
                dataset = new FilteredDataSet(databaseSequenceFilter, dataset);
            }
            operation.execute(connection, dataset);
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        }
    }

    public String getLogMessage() {
        StringBuilder result = new StringBuilder();
        result.append("Executing operation: ").append(type);
        result.append("on files: [ ");
        for (File f : sources) {
            result.append(f.getAbsolutePath()).append(" ");
        }
        result.append("]");
        result.append("with format: ").append(format);
        return result.toString();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Operation: ");
        result.append(" type=").append(type);
        result.append(", format=").append(format);
        result.append(", sources=[ ");
        for (File f : sources) {
            result.append(f.getAbsolutePath()).append(" ");
        }
        result.append("]");
        result.append(", operation=").append(databaseOperation);
        result.append(", nullToken=").append(nullToken);
        result.append(", ordered=").append(super.isOrdered());
        return result.toString();
    }
}