package org.dbunit.assertion.comparer.value;

import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * {@link ValueComparer} implementation for {@link Timestamp}s that verifies
 * actual value is within a low and high milliseconds tolerance range of
 * expected value.
 * <p>
 * Note: If actual and expected values are both null, the comparison passes.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
@Slf4j
@AllArgsConstructor
public class IsActualWithinToleranceOfExpectedTimestampValueComparer extends ValueComparerTemplateBase {
    public static final long ONE_SECOND_IN_MILLIS = 1000;
    public static final long ONE_MINUTE_IN_MILLIS = ONE_SECOND_IN_MILLIS * 60;

    private long lowToleranceValueInMillis;
    private long highToleranceValueInMillis;

    @Override
    protected boolean isExpected(final ITable expectedTable, final ITable actualTable, final int rowNum, final String columnName, final DataType dataType, final Object expectedValue, final Object actualValue) throws DatabaseUnitException {
        // handle nulls: prevent NPE and isExpected=true when both null
        if (expectedValue == null || actualValue == null) {
            return isExpectedWithNull(expectedValue, actualValue);
        }
        return isExpectedWithoutNull(expectedValue, actualValue, dataType);
    }

    /** Since one is a known null, isExpected=true when they equal. */
    protected boolean isExpectedWithNull(final Object expectedValue, final Object actualValue) {
        final boolean isExpected = expectedValue == actualValue;
        log.debug("isExpectedWithNull: {}, actualValue={}, expectedValue={}", isExpected, actualValue, expectedValue);
        return isExpected;
    }

    /** Neither is null so compare values with tolerance. */
    protected boolean isExpectedWithoutNull(final Object expectedValue, final Object actualValue, final DataType dataType) throws TypeCastException {
        assertNotNull("expectedValue is null.", expectedValue);
        assertNotNull("actualValue is null.", actualValue);
        final Object actualTimestamp = getCastedValue(actualValue, dataType);
        final long actualTime = convertValueToTimeInMillis(actualTimestamp);
        final Object expectedTimestamp = getCastedValue(expectedValue, dataType);
        final long expectedTime = convertValueToTimeInMillis(expectedTimestamp);
        final long diffTime = calcTimeDifference(actualTime, expectedTime);
        return isTolerant(diffTime);
    }

    protected Object getCastedValue(final Object value, final DataType type) throws TypeCastException {
        if (type == null || type == DataType.UNKNOWN) {
            return value;
        }
        return type.typeCast(value);
    }

    protected boolean isTolerant(final long diffTime) {
        final boolean isLowTolerant = diffTime >= lowToleranceValueInMillis;
        final boolean isHighTolerant = diffTime <= highToleranceValueInMillis;
        final boolean isTolerant = isLowTolerant && isHighTolerant;
        log.debug("isTolerant: {}, diffTime={}, lowToleranceValueInMillis={}, highToleranceValueInMillis={}", isTolerant, diffTime, lowToleranceValueInMillis, highToleranceValueInMillis);
        return isTolerant;
    }

    protected long convertValueToTimeInMillis(final Object timestampValue) {
        final Timestamp timestamp = (Timestamp) timestampValue;
        return timestamp.getTime();
    }

    protected long calcTimeDifference(final long actualTimeInMillis, final long expectedTimeInMillis) {
        final long diffTime = actualTimeInMillis - expectedTimeInMillis;
        final long diffTimeAbs = Math.abs(diffTime);
        log.debug("calcTimeDifference: actualTimeInMillis={}, expectedTimeInMillis={}, diffInMillisTime={}, diffTimeInMillisAbs={}", actualTimeInMillis, expectedTimeInMillis, diffTime, diffTimeAbs);
        return diffTimeAbs;
    }

    @Override
    protected String getFailPhrase() {
        return "not within tolerance range of " + lowToleranceValueInMillis + " - " + highToleranceValueInMillis + " milliseconds of";
    }
}