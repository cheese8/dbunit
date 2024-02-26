package org.dbunit.assertion.comparer.value;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * {@link ValueComparator} implementation that verifies actual value is equal to
 * expected value.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class IsActualEqualToExpectedValueComparer extends AbstractValueComparatorTemplate {
    @Override
    protected boolean isExpected(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        return dataType.compare(actualValue, expectedValue) == 0;
    }

    @Override
    protected String getFailPhrase() {
        return "not equal to";
    }
}