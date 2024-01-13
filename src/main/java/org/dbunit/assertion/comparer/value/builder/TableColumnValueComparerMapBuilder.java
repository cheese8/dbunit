package org.dbunit.assertion.comparer.value.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.assertion.comparer.value.ValueComparator;

/**
 * Convenience methods to help build the map of table name -> map of column name -> {@link ValueComparator}.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class TableColumnValueComparerMapBuilder {
    private final Map<String, Map<String, ValueComparator>> comparers = new HashMap<>();

    /**
     * Add all mappings from the specified table map to this builder.
     *
     * @return this for fluent syntax.
     */
    public TableColumnValueComparerMapBuilder add(final Map<String, Map<String, ValueComparator>> tableColumnValueComparers) {
        comparers.putAll(tableColumnValueComparers);
        return this;
    }

    /**
     * Add all mappings from the specified
     * {@link TableColumnValueComparerMapBuilder} builder to this builder.
     *
     * @return this for fluent syntax.
     */
    public TableColumnValueComparerMapBuilder add(final TableColumnValueComparerMapBuilder tableColumnValueComparerMapBuilder) {
        final Map<String, Map<String, ValueComparator>> map = tableColumnValueComparerMapBuilder.build();
        comparers.putAll(map);
        return this;
    }

    /**
     * Add all mappings from the specified column map to a column map for the
     * specified table in this builder.
     *
     * @return this for fluent syntax.
     */
    public TableColumnValueComparerMapBuilder add(final String tableName, final Map<String, ValueComparator> columnValueComparers) {
        final Map<String, ValueComparator> map = findOrMakeColumnMap(tableName);
        map.putAll(columnValueComparers);
        return this;
    }

    /**
     * Add all mappings from the specified {@link ColumnValueComparerMapBuilder}
     * builder to a column map for the specified table in this builder.
     *
     * @return this for fluent syntax.
     */
    public TableColumnValueComparerMapBuilder add(final String tableName, final ColumnValueComparerMapBuilder columnValueComparerMapBuilder) {
        final Map<String, ValueComparator> map = findOrMakeColumnMap(tableName);
        final Map<String, ValueComparator> columnMap = columnValueComparerMapBuilder.build();
        map.putAll(columnMap);
        return this;
    }

    /**
     * Add a table to column to {@link ValueComparator} mapping.
     *
     * @return this for fluent syntax.
     */
    public TableColumnValueComparerMapBuilder add(final String tableName, final String columnName, final ValueComparator valueComparator) {
        final Map<String, ValueComparator> map = findOrMakeColumnMap(tableName);
        map.put(columnName, valueComparator);
        return this;
    }

    /** @return The unmodifiable assembled map. */
    public Map<String, Map<String, ValueComparator>> build() {
        return Collections.unmodifiableMap(comparers);
    }

    protected Map<String, ValueComparator> findOrMakeColumnMap(final String tableName) {
        Map<String, ValueComparator> map = comparers.get(tableName);
        if (map == null) {
            map = makeColumnToValueComparerMap();
            comparers.put(tableName, map);
        }
        return map;
    }

    protected Map<String, ValueComparator> makeColumnToValueComparerMap() {
        return new HashMap<>();
    }
}