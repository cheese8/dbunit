package org.dbunit.assertion.comparer.value;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * Base class for {@link ValueComparator}s providing a template method and common
 * elements, mainly consistent log message and toString.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
@Slf4j
public abstract class ValueComparerBase implements ValueComparator {
    /**
     * Format String for consistent fail message; substitution strings are:
     * actual, fail phrase, expected.
     */
    public static final String BASE_FAIL_MSG = "Actual value='%s' is %s expected value='%s'";

    /**
     * {@inheritDoc}
     * <p>
     * This implementation calls
     * {@link #doCompare(ITable, ITable, int, String, DataType, Object, Object)}.
     */
    public String compare(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        final String failMessage;
        failMessage = doCompare(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue);
        log.debug("compare: rowNum={}, columnName={}, expectedValue={}, actualValue={}, failMessage={}", rowNum, columnName, expectedValue, actualValue, failMessage);
        return failMessage;
    }

    /**
     * Do the comparison and return a fail message or null if comparison passes.
     *
     * @see ValueComparator#compare(ITable, ITable, int, String, DataType, Object,
     * Object)
     */
    protected abstract String doCompare(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException;

    @Override
    public String toString() {
        return getClass().getName();
    }
}