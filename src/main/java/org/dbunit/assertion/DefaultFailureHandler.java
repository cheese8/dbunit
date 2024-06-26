/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2008, DbUnit.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.assertion;

import java.util.Arrays;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ColumnFilterTable;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchColumnException;

/**
 * Default implementation of the {@link FailureHandler}.
 *
 * @author gommma (gommma AT users.sourceforge.net)
 * @since 2.4.0
 */
@Slf4j
@NoArgsConstructor
public class DefaultFailureHandler implements FailureHandler {
    private String[] additionalColumnInfo;
    private FailureFactory failureFactory = new DefaultFailureFactory();

    /**
     * Create a default failure handler
     *
     * @param additionalColumnInfo the column names of the columns for which additional
     *                             information should be printed when an assertion failed.
     */
    public DefaultFailureHandler(final Column[] additionalColumnInfo) {
        // Null-safe access
        if (additionalColumnInfo != null) {
            this.additionalColumnInfo = Columns.getColumnNames(additionalColumnInfo);
        }
    }

    /**
     * Create a default failure handler
     *
     * @param additionalColumnInfo the column names of the columns for which additional
     *                             information should be printed when an assertion failed.
     */
    public DefaultFailureHandler(final String[] additionalColumnInfo) {
        this.additionalColumnInfo = additionalColumnInfo;
    }

    /**
     * @param failureFactory The {@link FailureFactory} to be used for creating assertion
     *                       errors.
     */
    public void setFailureFactory(final FailureFactory failureFactory) {
        if (failureFactory == null) {
            throw new NullPointerException("The parameter 'failureFactory' must not be null");
        }
        this.failureFactory = failureFactory;
    }

    public Error createFailure(final String message, final String expected, final String actual) {
        return this.failureFactory.createFailure(message, expected, actual);
    }

    public Error createFailure(final String message) {
        return this.failureFactory.createFailure(message);
    }

    public String getAdditionalInfo(final ITable expectedTable, final ITable actualTable, final int row, final String columnName) {
        // add custom column values information for better identification of
        // mismatching rows
        return buildAdditionalColumnInfo(expectedTable, actualTable, row);
    }

    private String buildAdditionalColumnInfo(final ITable expectedTable, final ITable actualTable, final int rowIndex) {
        log.debug("buildAdditionalColumnInfo(expectedTable={}, actualTable={}, rowIndex={}, additionalColumnInfo={}) - start", expectedTable, actualTable, rowIndex, additionalColumnInfo);

        // No columns specified
        if (additionalColumnInfo == null || additionalColumnInfo.length == 0) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("Additional row info:");
        for (final String columnName : additionalColumnInfo) {
            final Object expectedKeyValue = getColumnValue(expectedTable, rowIndex, columnName);
            final Object actualKeyValue = getColumnValue(actualTable, rowIndex, columnName);

            sb.append(" ('");
            sb.append(columnName);
            sb.append("': expected=<");
            sb.append(expectedKeyValue);
            sb.append(">, actual=<");
            sb.append(actualKeyValue);
            sb.append(">)");
        }

        return sb.toString();
    }

    protected Object getColumnValue(final ITable table, final int rowIndex, final String columnName) {
        Object value;
        try {
            // Get the ITable object to be used for showing the column values
            // (needed in case of Filtered tables)
            final ITable tableForCol = getTableForColumn(table, columnName);
            value = tableForCol.getValue(rowIndex, columnName);
        } catch (final DataSetException e) {
            value = makeAdditionalColumnInfoErrorMessage(columnName, e);
        }
        return value;
    }

    protected String makeAdditionalColumnInfoErrorMessage(final String columnName, final DataSetException e) {
        final String msg = "Exception creating more info for column '" + columnName + "': " + e.getClass().getName() + ": " + e.getMessage();
        log.warn(msg, e);
        return " (!!!!! " + msg + ")";
    }

    /**
     * @param table      The table which might be a decorated table
     * @param columnName The column name for which a table is searched
     * @return The table that as a column with the given name
     * @throws DataSetException If no table could be found having a column with the given
     *                          name
     */
    private ITable getTableForColumn(final ITable table, final String columnName) throws DataSetException {
        final ITableMetaData tableMetaData = table.getTableMetaData();
        try {
            tableMetaData.getColumnIndex(columnName);
            // if the column index was resolved the table contains the given
            // column. So just use this table
            return table;
        } catch (final NoSuchColumnException e) {
            // If the column was not found check for filtered table
            if (table instanceof ColumnFilterTable) {
                final ITableMetaData originalMetaData = ((ColumnFilterTable) table).getOriginalMetaData();
                originalMetaData.getColumnIndex(columnName);
                // If we get here the column exists - return the table since it
                // is not filtered in the CompositeTable.
                return table;
            } else {
                // Column not available in the table - rethrow the exception
                throw e;
            }
        }
    }

    public void handle(final Difference diff) {
        final String msg = buildMessage(diff);
        // Throw the assertion error
        throw this.createFailure(msg, String.valueOf(diff.getExpectedValue()), String.valueOf(diff.getActualValue()));
    }

    public void handle(final List<Difference> diffList) {
        StringBuilder sb = new StringBuilder(String.format("%s different places was collected as below:\n", diffList.size()));
        for (Difference each : diffList) {
            String expectedAndActual = "(expected=<%s>, actual=<%s>)";
            sb.append(buildMessage(each) + " " + String.format(expectedAndActual, each.getExpectedValue(), each.getActualValue())).append("\n");
            //addFailMessage(each, sb);
        }
        //final String msg = buildMessage(sb.toString());
        // Throw the assertion error
        throw this.createFailure(sb.toString());
        //throw this.createFailure(sb.toString(), String.valueOf(diff.getExpectedValue()), String.valueOf(diff.getActualValue()));
    }

    protected String buildMessage(final Difference diff) {
        final StringBuilder builder = new StringBuilder(200);

        final int rowNum = diff.getRowIndex();
        final String columnName = diff.getColumnName();
        final ITable expectedTable = diff.getExpectedTable();
        final ITable actualTable = diff.getActualTable();

        addFailMessage(diff, builder);

        final String expectedTableName = expectedTable.getTableMetaData().getTableName();

        // example message:
        // "value (table=MYTAB, row=232, column=MYCOL, Additional row info:
        // (column=MyIdCol, expected=444, actual=555)): expected:<123> but
        // was:<1234>"
        builder.append("value (table=").append(expectedTableName);
        builder.append(", row=").append(rowNum);
        builder.append(", col=").append(columnName);

        final String additionalInfo = this.getAdditionalInfo(expectedTable, actualTable, rowNum, columnName);
        if (additionalInfo != null && !additionalInfo.trim().equals("")) {
            builder.append(", ").append(additionalInfo);
        }

        builder.append(")");

        return builder.toString();
    }

    protected void addFailMessage(final Difference diff, final StringBuilder builder) {
        final String failMessage = diff.getFailMessage();
        final boolean isFailMessage = isFailMessage(failMessage);
        if (isFailMessage) {
            builder.append(failMessage).append(": ");
        }
    }

    protected boolean isFailMessage(final String failMessage) {
        return failMessage != null && !failMessage.isEmpty();
    }

    @Override
    public String toString() {
        return DefaultFailureHandler.class.getName() + "[" + "_additionalColumnInfo=" + (additionalColumnInfo == null ? "null" : Arrays.asList(additionalColumnInfo).toString()) + "]";
    }
}