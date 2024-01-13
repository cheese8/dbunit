/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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

import java.util.Arrays;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.assertion.comparer.value.ValueComparator;
import org.dbunit.assertion.comparer.value.verifier.DefaultVerifyTableDefinitionVerifier;
import org.dbunit.assertion.comparer.value.verifier.VerifyTableDefinitionVerifier;
import org.dbunit.util.Assert;

/**
 * Defines a database table to verify (assert on data), specifying include and
 * exclude column filters and optional {@link ValueComparator}s.
 *
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class VerifyTableDefinition {
    /** The name of the table. */
    @Getter
    private final String tableName;
    /** The columns to exclude in table comparisons. */
    @Getter
    private final String[] columnExclusionFilters;
    /** The columns to include in table comparisons. */
    @Getter
    private final String[] columnInclusionFilters;
    /**
     * {@link ValueComparator} to use with column value comparisons when the
     * column name for the table is not in the {@link VerifyTableDefinition#columnValueComparators}
     * {@link Map}. Can be <code>null</code> and will default.
     *
     * @since 2.6.0
     */
    @Getter
    private final ValueComparator defaultValueComparator;
    /**
     * Map of column names to {@link ValueComparator}s to use for comparison.
     * 
     * @since 2.6.0
     */
    @Getter
    private final Map<String, ValueComparator> columnValueComparators;
    @Getter @Setter
    private VerifyTableDefinitionVerifier verifyTableDefinitionVerifier = new DefaultVerifyTableDefinitionVerifier();

    /**
     * Create a valid instance with all columns compared except exclude the
     * specified columns.
     *
     * @param table
     *            The name of the table - required.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons; null or empty array to exclude no columns.
     */
    public VerifyTableDefinition(final String table, final String[] excludeColumns) {
        this(table, excludeColumns, null, null, null);
    }

    /**
     * Create a valid instance with all columns compared and use the specified
     * defaultValueComparer for all column comparisons not in the
     * columnValueComparers {@link Map}.
     *
     * @param table
     *            The name of the table - required.
     * @param defaultValueComparator
     *            {@link ValueComparator} to use with column value comparisons
     *            when the column name for the table is not in the
     *            columnValueComparers {@link Map}. Can be <code>null</code> and
     *            will default.
     * @param columnValueComparers
     *            {@link Map} of {@link ValueComparator}s to use for specific
     *            columns. Key is column name, value is {@link ValueComparator} to
     *            use for comparison of that column. Can be <code>null</code>
     *            and will default to defaultValueComparer for all columns in
     *            all tables.
     * @since 2.6.0
     */
    public VerifyTableDefinition(final String table, final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparers) {
        this(table, null, null, defaultValueComparator, columnValueComparers);
    }

    /**
     * Create a valid instance with all columns compared and exclude the
     * specified columns, and use the specified defaultValueComparer for all
     * column comparisons not in the columnValueComparers {@link Map}.
     *
     * @param table
     *            The name of the table - required.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons; null or empty array to exclude no columns.
     * @param defaultValueComparator
     *            {@link ValueComparator} to use with column value comparisons
     *            when the column name for the table is not in the
     *            columnValueComparers {@link Map}. Can be <code>null</code> and
     *            will default.
     * @param columnValueComparers
     *            {@link Map} of {@link ValueComparator}s to use for specific
     *            columns. Key is column name, value is {@link ValueComparator} to
     *            use for comparison of that column. Can be <code>null</code>
     *            and will default to defaultValueComparer for all columns in
     *            all tables.
     * @since 2.6.0
     */
    public VerifyTableDefinition(final String table, final String[] excludeColumns, final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparers) {
        this(table, excludeColumns, null, defaultValueComparator, columnValueComparers);
    }

    /**
     * Create a valid instance specifying exclude and include columns.
     *
     * @param table
     *            The name of the table.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons; null or empty array to exclude no columns.
     * @param includeColumns
     *            The columns in the table to include in expected vs actual
     *            comparisons; null to include all columns, empty array to
     *            include no columns.
     */
    public VerifyTableDefinition(final String table, final String[] excludeColumns, final String[] includeColumns) {
        this(table, excludeColumns, includeColumns, null, null);
    }

    /**
     * Create a valid instance specifying exclude and include columns and use
     * the specified defaultValueComparer for all column comparisons not in the
     * columnValueComparers {@link Map}.
     *
     * @param table
     *            The name of the table.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons; null or empty array to exclude no columns.
     * @param includeColumns
     *            The columns in the table to include in expected vs actual
     *            comparisons; null to include all columns, empty array to
     *            include no columns.
     * @param defaultValueComparator
     *            {@link ValueComparator} to use with column value comparisons
     *            when the column name for the table is not in the
     *            columnValueComparers {@link Map}. Can be <code>null</code> and
     *            will default.
     * @param columnValueComparators
     *            {@link Map} of {@link ValueComparator}s to use for specific
     *            columns. Key is column name, value is {@link ValueComparator} to
     *            use for comparison of that column. Can be <code>null</code>
     *            and will default to defaultValueComparer for all columns in
     *            all tables.
     * @since 2.6.0
     */
    public VerifyTableDefinition(final String table, final String[] excludeColumns, final String[] includeColumns, final ValueComparator defaultValueComparator, final Map<String, ValueComparator> columnValueComparators) {
        Assert.assertThat(StringUtils.isNotBlank(table), new IllegalArgumentException("table is null."));
        tableName = table;
        columnExclusionFilters = excludeColumns;
        columnInclusionFilters = includeColumns;
        this.defaultValueComparator = defaultValueComparator;
        this.columnValueComparators = columnValueComparators;
        verifyTableDefinitionVerifier.verify(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final String exclusionString = arrayToString(columnExclusionFilters);
        final String inclusionString = arrayToString(columnInclusionFilters);
        return "tableName='" + tableName + "', columnExclusionFilters='" + exclusionString + "', columnInclusionFilters='" + inclusionString + "', defaultValueComparer='" + defaultValueComparator + "', columnValueComparators='" + columnValueComparators + "'";
    }

    protected String arrayToString(final String[] array) {
        return array == null ? "" : Arrays.toString(array);
    }
}