package org.dbunit.assertion.comparer.value;

import java.util.Map;

/**
 * Default {@link ValueComparator}s, used when one is not specified by a test.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public interface ValueComparerDefaults {
    ValueComparator getDefaultValueComparer();

    Map<String, Map<String, ValueComparator>> getDefaultTableColumnValueComparerMap();

    Map<String, ValueComparator> getDefaultColumnValueComparerMapForTable(String tableName);
}