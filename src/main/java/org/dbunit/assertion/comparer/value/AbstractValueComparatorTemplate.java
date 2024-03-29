package org.dbunit.assertion.comparer.value;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * Base class for {@link ValueComparator}s, providing template methods and common
 * elements.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public abstract class AbstractValueComparatorTemplate extends AbstractValueComparator {
    /**
     * {@inheritDoc}
     * <p>
     * This implementation calls
     * {@link #isExpected(ITable, ITable, int, String, DataType, Object, Object)}.
     *
     * @see ValueComparator#compare(ITable, ITable, int, String, DataType, Object,
     * Object)
     */
    @Override
    protected String doCompare(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        final boolean isExpected = isExpected(expectedTable, actualTable, rowNum, columnName, dataType, expectedValue, actualValue);
        if (isExpected) {
            return null;
        }
        return makeFailMessage(expectedValue, actualValue);
    }

    /**
     * Makes the fail message using {@link #getFailPhrase()}.
     *
     * @return the formatted fail message with the fail phrase.
     */
    protected String makeFailMessage(final Object expectedValue, final Object actualValue) {
        final String failPhrase = getFailPhrase();
        return String.format(FAIL_MSG, actualValue, failPhrase, expectedValue);
    }

    /**
     * @return true if comparing actual to expected is as expected.
     */
    protected abstract boolean isExpected(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException;

    /**
     * @return The text snippet for substitution in {@link #FAIL_MSG}.
     */
    protected abstract String getFailPhrase();
}