package org.dbunit.assertion.comparer.value;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * Use a {@link ValueComparerSelector} to select a {@link ValueComparator} for the
 * column from a {@link Map} of them.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class ConditionalSelectorMultiValueComparer extends AbstractValueComparator {
    private final ValueComparerSelector valueComparerSelector;
    private final Map<Object, ValueComparator> valueComparators;

    public ConditionalSelectorMultiValueComparer(final Map<Object, ValueComparator> valueComparators, final ValueComparerSelector valueComparerSelector) {
        assertNotNull("valueComparerSelector is null.", valueComparerSelector);
        assertNotNull("valueComparators is null.", valueComparators);
        this.valueComparerSelector = valueComparerSelector;
        this.valueComparators = valueComparators;
    }

    @Override
    public String doCompare(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        final ValueComparator valueComparator = valueComparerSelector.select(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue, valueComparators);
        if (valueComparator == null) {
            final String msg = "No ValueComparer found by valueComparerSelector=" + valueComparerSelector + " in map=" + valueComparators;
            throw new IllegalStateException(msg);
        }
        return valueComparator.compare(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue);
    }

    @Override
    public String toString() {
        return super.toString() + ": [valueComparerSelector=" + valueComparerSelector.getClass().getName() + ", inValuesValueComparer=" + valueComparators + "]";
    }
}