package org.dbunit.assertion.comparer.value.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.assertion.comparer.value.ValueComparator;

/**
 * Convenience methods to help build the map of column name ->
 * {@link ValueComparator}.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class ColumnValueComparerMapBuilder {
    private final Map<String, ValueComparator> comparators = new HashMap<>();

    /**
     * Add a columnName to {@link ValueComparator} mapping.
     *
     * @return this for fluent syntax.
     */
    public ColumnValueComparerMapBuilder add(final String columnName, final ValueComparator valueComparator) {
        comparators.put(columnName, valueComparator);
        return this;
    }

    /** @return The assembled map. */
    public Map<String, ValueComparator> build() {
        return Collections.unmodifiableMap(comparators);
    }
}