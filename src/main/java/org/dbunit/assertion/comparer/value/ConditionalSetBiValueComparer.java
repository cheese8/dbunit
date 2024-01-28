package org.dbunit.assertion.comparer.value;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * Use one of two {@link ValueComparator}s based on a value present or not in a
 * set of values.
 * <p>
 * When the value returned by the
 * {@link ConditionalSetBiValueComparer#actualValueFactory} is in
 * {@link ConditionalSetBiValueComparer#values}, use the
 * {@link ConditionalSetBiValueComparer#inValuesValueComparator} on the row;
 * otherwise, use the
 * {@link ConditionalSetBiValueComparer#notInValuesValueComparator} on the row.
 *
 * @param <T> The type of the value used to determine which
 *            {@link ValueComparator} to use.
 * @author Jeff Jensen
 * @since 2.6.0
 */
@Slf4j
public class ConditionalSetBiValueComparer<T> extends ValueComparerBase {
    private final ValueFactory<T> actualValueFactory;
    private final Set<T> values;
    private final ValueComparator inValuesValueComparator;
    private final ValueComparator notInValuesValueComparator;

    /**
     * @param actualValueFactory         Factory to make the value to lookup in the values list.
     * @param values                     List of values that mean to use the inValuesValueComparer.
     * @param inValuesValueComparator    The {@link ValueComparator} used when the value from the
     *                                   actualValueFactory is in the values map.
     * @param notInValuesValueComparator The {@link ValueComparator} used when the value from the
     *                                   actualValueFactory is not in the values map.
     */
    public ConditionalSetBiValueComparer(final ValueFactory<T> actualValueFactory, final Set<T> values, final ValueComparator inValuesValueComparator, final ValueComparator notInValuesValueComparator) {
        assertNotNull("actualValueFactory is null.", actualValueFactory);
        assertNotNull("values is null.", values);
        assertNotNull("inValuesValueComparer is null.", inValuesValueComparator);
        assertNotNull("notInValuesValueComparer is null.", notInValuesValueComparator);
        this.actualValueFactory = actualValueFactory;
        this.values = values;
        this.inValuesValueComparator = inValuesValueComparator;
        this.notInValuesValueComparator = notInValuesValueComparator;
    }

    @Override
    public String doCompare(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        final boolean isInValues = isActualValueInValues(actualTable, rowNum);
        if (isInValues) {
            return inValuesValueComparator.compare(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue);
        }
        return notInValuesValueComparator.compare(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue);
    }

    protected boolean isActualValueInValues(final ITable actualTable, final int rowNum) throws DataSetException {
        final T actualValue = actualValueFactory.make(actualTable, rowNum);
        final boolean isListContains = values.contains(actualValue);
        log.debug("isActualValueInValues: actualValue={}, isListContains={}", actualValue, isListContains);
        return isListContains;
    }

    @Override
    public String toString() {
        return super.toString() + ": [values=" + values + ", inValuesValueComparer=" + inValuesValueComparator + ", notInValuesValueComparer=" + notInValuesValueComparator + "]";
    }
}