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

package org.dbunit;

import java.sql.SQLException;
import java.util.Map;

import org.dbunit.assertion.DbUnitAssert;
import org.dbunit.assertion.DbUnitValueComparerAssert;
import org.dbunit.assertion.FailureHandler;
import org.dbunit.assertion.comparer.value.ValueComparator;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

/**
 * Provides static methods for the most common DbUnit assertion needs.
 * <p>
 * Although the methods are static, they rely on a {@link DbUnitAssert} instance
 * to do the work. So, if you need to customize this class behavior, you can
 * create your own {@link DbUnitAssert} extension.
 *
 * @author Manuel Laflamme
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.3 (Mar 22, 2002)
 */
public class Assertion {
    /**
     * Assert using equals comparisons.
     */
    private static final DbUnitAssert EQUALS_INSTANCE = new DbUnitAssert();

    /**
     * Assert using compare comparisons. @since 2.6.0
     */
    private static final DbUnitValueComparerAssert VALUE_COMPARE_INSTANCE = new DbUnitValueComparerAssert();

    private Assertion() {
        throw new UnsupportedOperationException("this class has only static methods");
    }

    /**
     * @see DbUnitAssert#assertEqualsIgnoreCols(IDataSet, IDataSet, String,
     * String[], FailureHandler)
     */
    public static void assertEqualsIgnoreCols(final IDataSet expectedDataset, final IDataSet actualDataset, final String tableName,
                                              final String[] ignoreCols, FailureHandler failureHandler) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEqualsIgnoreCols(expectedDataset, actualDataset, tableName, ignoreCols, failureHandler);
    }

    /**
     * @see DbUnitAssert#assertEqualsIgnoreCols(ITable, ITable, String[], FailureHandler)
     */
    public static void assertEqualsIgnoreCols(final ITable expectedTable, final ITable actualTable,
                                              final String[] ignoreCols, FailureHandler failureHandler) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEqualsIgnoreCols(expectedTable, actualTable, ignoreCols, failureHandler);
    }

    /**
     * @see DbUnitAssert#assertEqualsByQuery(IDataSet, IDatabaseConnection,
     * String, String, String[])
     */
    public static void assertEqualsByQuery(final IDataSet expectedDataset, final IDatabaseConnection connection, final String sqlQuery,
                                           final String tableName, final String[] ignoreCols) throws DatabaseUnitException, SQLException {
        EQUALS_INSTANCE.assertEqualsByQuery(expectedDataset, connection, sqlQuery, tableName, ignoreCols);
    }

    /**
     * @see DbUnitAssert#assertEqualsByQuery(ITable, IDatabaseConnection,
     * String, String, String[])
     */
    public static void assertEqualsByQuery(final ITable expectedTable, final IDatabaseConnection connection, final String tableName,
                                           final String sqlQuery, final String[] ignoreCols) throws DatabaseUnitException, SQLException {
        EQUALS_INSTANCE.assertEqualsByQuery(expectedTable, connection, tableName, sqlQuery, ignoreCols);
    }

    /**
     * @see DbUnitAssert#assertEquals(IDataSet, IDataSet)
     */
    public static void assertEquals(final IDataSet expectedDataSet, final IDataSet actualDataSet) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEquals(expectedDataSet, actualDataSet);
    }

    /**
     * @see DbUnitAssert#assertEquals(IDataSet, IDataSet, FailureHandler)
     * @since 2.4
     */
    public static void assertEquals(final IDataSet expectedDataSet, final IDataSet actualDataSet,
                                    final FailureHandler failureHandler) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEquals(expectedDataSet, actualDataSet, failureHandler);
    }

    /**
     * @see DbUnitAssert#assertEquals(ITable, ITable)
     */
    public static void assertEquals(final ITable expectedTable, final ITable actualTable) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEquals(expectedTable, actualTable);
    }

    /**
     * @see DbUnitAssert#assertEquals(ITable, ITable, Column[])
     */
    public static void assertEquals(final ITable expectedTable, final ITable actualTable, final Column[] additionalColumnInfo) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEquals(expectedTable, actualTable, additionalColumnInfo);
    }

    /**
     * @see DbUnitAssert#assertEquals(ITable, ITable, FailureHandler)
     * @since 2.4
     */
    public static void assertEquals(final ITable expectedTable, final ITable actualTable, final FailureHandler failureHandler) throws DatabaseUnitException {
        EQUALS_INSTANCE.assertEquals(expectedTable, actualTable, failureHandler);
    }

    /**
     * @see DbUnitValueComparerAssert#assertWithValueComparer(IDataSet,
     * IDataSet, ValueComparator, Map)
     * @since 2.6.0
     */
    public static void assertWithValueComparer(final IDataSet expectedDataSet, final IDataSet actualDataSet, final ValueComparator defaultValueComparator,
                                               final Map<String, Map<String, ValueComparator>> tableColumnValueComparators) throws DatabaseUnitException {
        VALUE_COMPARE_INSTANCE.assertWithValueComparer(expectedDataSet, actualDataSet, defaultValueComparator, tableColumnValueComparators);
    }

    /**
     * @see DbUnitValueComparerAssert#assertWithValueComparer(ITable, ITable,
     * ValueComparator, Map)
     * @since 2.6.0
     */
    public static void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final ValueComparator defaultValueComparator,
                                               final Map<String, ValueComparator> columnValueComparators) throws DatabaseUnitException {
        VALUE_COMPARE_INSTANCE.assertWithValueComparer(expectedTable, actualTable, defaultValueComparator, columnValueComparators);
    }

    /**
     * @see DbUnitValueComparerAssert#assertWithValueComparer(IDataSet,
     * IDataSet, FailureHandler, ValueComparator, Map)
     * @since 2.6.0
     */
    public static void assertWithValueComparer(final IDataSet expectedDataSet, final IDataSet actualDataSet, final FailureHandler failureHandler,
                                               final ValueComparator defaultValueComparator, final Map<String, Map<String, ValueComparator>> tableColumnValueComparators) throws DatabaseUnitException {
        VALUE_COMPARE_INSTANCE.assertWithValueComparer(expectedDataSet, actualDataSet, failureHandler, defaultValueComparator, tableColumnValueComparators);
    }

    /**
     * @see DbUnitValueComparerAssert#assertWithValueComparer(ITable, ITable,
     * Column[], ValueComparator, Map)
     * @since 2.6.0
     */
    public static void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final Column[] additionalColumnInfo,
                                               final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparators) throws DatabaseUnitException {
        VALUE_COMPARE_INSTANCE.assertWithValueComparer(expectedTable, actualTable, additionalColumnInfo, defaultValueComparator, columnValueComparators);
    }

    /**
     * @see DbUnitValueComparerAssert#assertWithValueComparer(ITable, ITable,
     * FailureHandler, ValueComparator, Map)
     * @since 2.6.0
     */
    public static void assertWithValueComparer(final ITable expectedTable, final ITable actualTable, final FailureHandler failureHandler,
                                               final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparators) throws DatabaseUnitException {
        VALUE_COMPARE_INSTANCE.assertWithValueComparer(expectedTable, actualTable, failureHandler, defaultValueComparator, columnValueComparators);
    }
}