package org.dbunit.assertion.comparer.value;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation for the {@link ValueComparerDefaults}.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class DefaultValueComparerDefaults implements ValueComparerDefaults {
    @Override
    public ValueComparator getDefaultValueComparer()
    {
        return ValueComparers.isActualEqualToExpected;
    }

    @Override
    public Map<String, Map<String, ValueComparator>> getDefaultTableColumnValueComparerMap() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ValueComparator> getDefaultColumnValueComparerMapForTable(final String tableName) {
        return Collections.emptyMap();
    }
}