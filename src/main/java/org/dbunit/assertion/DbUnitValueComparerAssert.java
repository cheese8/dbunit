package org.dbunit.assertion;

import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.comparer.value.ValueComparator;
import org.dbunit.assertion.comparer.value.ValueComparerDefaults;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

/**
 * DbUnit assertions using {@link ValueComparator}s for the column comparisons.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class DbUnitValueComparerAssert extends DbUnitAssertBase {
    /**
     * Asserts the two specified {@link IDataSet}s comparing their columns using
     * the default {@link ValueComparator} and handles failures using the default
     * {@link FailureHandler}. This method ignores the table names, the columns
     * order, the columns data type, and which columns are composing the primary
     * keys.
     *
     * @param expectedDataSet {@link IDataSet} containing all expected results.
     * @param actualDataSet   {@link IDataSet} containing all actual results.
     */
    public void assertWithValueComparer(final IDataSet expectedDataSet, final IDataSet actualDataSet) throws DatabaseUnitException {
        final ValueComparator defaultValueComparator = valueComparerDefaults.getDefaultValueComparer();
        assertWithValueComparer(expectedDataSet, actualDataSet, defaultValueComparator);
    }

    /**
     * Asserts the two specified {@link IDataSet}s comparing their columns using
     * the specified defaultValueComparer and handles failures using the default
     * {@link FailureHandler}. This method ignores the table names, the columns
     * order, the columns data type, and which columns are composing the primary
     * keys.
     *
     * @param expectedDataSet        {@link IDataSet} containing all expected results.
     * @param actualDataSet          {@link IDataSet} containing all actual results.
     * @param defaultValueComparator {@link ValueComparator} to use with all column value
     *                               comparisons. Can be <code>null</code> and will default to
     *                               {@link ValueComparerDefaults#getDefaultValueComparer()}.
     */
    public void assertWithValueComparer(final IDataSet expectedDataSet, final IDataSet actualDataSet, final ValueComparator defaultValueComparator) throws DatabaseUnitException {
        final Map<String, Map<String, ValueComparator>> tableColumnValueComparers = valueComparerDefaults.getDefaultTableColumnValueComparerMap();
        assertWithValueComparer(expectedDataSet, actualDataSet, defaultValueComparator, tableColumnValueComparers);
    }

    /**
     * Asserts the two specified {@link IDataSet}s comparing their columns using
     * the specified columnValueComparers or defaultValueComparer and handles
     * failures using the default {@link FailureHandler}. This method ignores
     * the table names, the columns order, the columns data type, and which
     * columns are composing the primary keys.
     *
     * @param expectedDataSet           {@link IDataSet} containing all expected results.
     * @param actualDataSet             {@link IDataSet} containing all actual results.
     * @param defaultValueComparator    {@link ValueComparator} to use with column value comparisons
     *                                  when the column name for the table is not in the
     *                                  tableColumnValueComparers {@link Map}. Can be
     *                                  <code>null</code> and will default to
     *                                  {@link ValueComparerDefaults#getDefaultValueComparer()}.
     * @param tableColumnValueComparers {@link Map} of {@link ValueComparator}s to use for specific
     *                                  tables and columns. Key is table name, value is {@link Map} of
     *                                  column name in the table to {@link ValueComparator}s. Can be
     *                                  <code>null</code> and will default to using
     *                                  {@link ValueComparerDefaults#getDefaultColumnValueComparerMapForTable(String)} or,
     *                                  if that is empty, defaultValueComparer for all columns in all
     *                                  tables.
     */
    public void assertWithValueComparer(final IDataSet expectedDataSet, final IDataSet actualDataSet, final ValueComparator defaultValueComparator, final Map<String, Map<String, ValueComparator>> tableColumnValueComparers) throws DatabaseUnitException {
        final FailureHandler failureHandler = getDefaultFailureHandler();
        assertWithValueComparer(expectedDataSet, actualDataSet, failureHandler, defaultValueComparator, tableColumnValueComparers);
    }

    /**
     * Asserts the two specified {@link ITable}s comparing their columns using
     * the default {@link ValueComparator} and handles failures using the default
     * {@link FailureHandler}. This method ignores the table names, the columns
     * order, the columns data type, and which columns are composing the primary
     * keys.
     *
     * @param expectedTable {@link ITable} containing all expected results.
     * @param actualTable   {@link ITable} containing all actual results.
     */
    public void assertWithValueComparer(final ITable expectedTable, final ITable actualTable) throws DatabaseUnitException {
        final ValueComparator defaultValueComparator = valueComparerDefaults.getDefaultValueComparer();
        assertWithValueComparer(expectedTable, actualTable, defaultValueComparator);
    }

    /**
     * Asserts the two specified {@link ITable}s comparing their columns using
     * the specified defaultValueComparer and handles failures using the default
     * {@link FailureHandler}. This method ignores the table names, the columns
     * order, the columns data type, and which columns are composing the primary
     * keys.
     *
     * @param expectedTable          {@link ITable} containing all expected results.
     * @param actualTable            {@link ITable} containing all actual results.
     * @param defaultValueComparator {@link ValueComparator} to use with all column value
     *                               comparisons. Can be <code>null</code> and will default to
     *                               {@link ValueComparerDefaults#getDefaultValueComparer()}.
     */
    public void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final ValueComparator defaultValueComparator) throws DatabaseUnitException {
        final String tableName = expectedTable.getTableMetaData().getTableName();
        final Map<String, ValueComparator> columnValueComparers = valueComparerDefaults.getDefaultColumnValueComparerMapForTable(tableName);
        assertWithValueComparer(expectedTable, actualTable, defaultValueComparator, columnValueComparers);
    }

    /**
     * Asserts the two specified {@link ITable}s comparing their columns using
     * the specified columnValueComparers or defaultValueComparer and handles
     * failures using the default {@link FailureHandler}. This method ignores
     * the table names, the columns order, the columns data type, and which
     * columns are composing the primary keys.
     *
     * @param expectedTable          {@link ITable} containing all expected results.
     * @param actualTable            {@link ITable} containing all actual results.
     * @param defaultValueComparator {@link ValueComparator} to use with column value comparisons
     *                               when the column name for the table is not in the
     *                               columnValueComparers {@link Map}. Can be <code>null</code> and
     *                               will default to {@link ValueComparerDefaults#getDefaultValueComparer()}.
     * @param columnValueComparers   {@link Map} of {@link ValueComparator}s to use for specific
     *                               columns. Key is column name in the table, value is
     *                               {@link ValueComparator} to use in comparing expected to actual
     *                               column values. Can be <code>null</code> and will default to
     *                               using
     *                               {@link ValueComparerDefaults#getDefaultColumnValueComparerMapForTable(String)} or,
     *                               if that is empty, defaultValueComparer for all columns in the
     *                               table.
     */
    public void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparers) throws DatabaseUnitException {
        final FailureHandler failureHandler = getDefaultFailureHandler();
        assertWithValueComparer(expectedTable, actualTable, failureHandler, defaultValueComparator, columnValueComparers);
    }

    /**
     * Asserts the two specified {@link ITable}s comparing their columns using
     * the specified columnValueComparers or defaultValueComparer and handles
     * failures using the default {@link FailureHandler}, using
     * additionalColumnInfo, if specified. This method ignores the table names,
     * the columns order, the columns data type, and which columns are composing
     * the primary keys.
     *
     * @param expectedTable          {@link ITable} containing all expected results.
     * @param actualTable            {@link ITable} containing all actual results.
     * @param additionalColumnInfo   The columns to be printed out if the assert fails because of a
     *                               data mismatch. Provides some additional column values that may
     *                               be useful to quickly identify the columns for which the
     *                               mismatch occurred (for example a primary key column). Can be
     *                               <code>null</code>
     * @param defaultValueComparator {@link ValueComparator} to use with column value comparisons
     *                               when the column name for the table is not in the
     *                               columnValueComparers {@link Map}. Can be <code>null</code> and
     *                               will default to {@link ValueComparerDefaults#getDefaultValueComparer()}.
     * @param columnValueComparers   {@link Map} of {@link ValueComparator}s to use for specific
     *                               columns. Key is column name in the table, value is
     *                               {@link ValueComparator} to use in comparing expected to actual
     *                               column values. Can be <code>null</code> and will default to
     *                               using
     *                               {@link ValueComparerDefaults#getDefaultColumnValueComparerMapForTable(String)} or,
     *                               if that is empty, defaultValueComparer for all columns in the
     *                               table.
     */
    public void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final Column[] additionalColumnInfo, final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparers) throws DatabaseUnitException {
        final FailureHandler failureHandler = getDefaultFailureHandler(additionalColumnInfo);
        assertWithValueComparer(expectedTable, actualTable, failureHandler, defaultValueComparator, columnValueComparers);
    }
}