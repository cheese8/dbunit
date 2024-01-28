package org.dbunit.assertion.comparer.value;

import static org.dbunit.assertion.comparer.value.IsActualWithinToleranceOfExpectedTimestampValueComparer.ONE_MINUTE_IN_MILLIS;
import static org.dbunit.assertion.comparer.value.IsActualWithinToleranceOfExpectedTimestampValueComparer.ONE_SECOND_IN_MILLIS;

/**
 * Convenience set of common {@link ValueComparator} instances.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public abstract class ValueComparers {
    protected ValueComparers() {
    }

    /**
     * @see IsActualEqualToExpectedValueComparer
     */
    public static final ValueComparator isActualEqualToExpected = new IsActualEqualToExpectedValueComparer();

    /**
     * Same as {@link #isActualEqualToExpected} but its {@link ValueComparator}
     * fail message is an empty String (the
     * {@link org.dbunit.assertion.Difference} fail message still exists). Use
     * this one mainly for backwards compatibility so the
     * {@link org.dbunit.assertion.Difference} message stays the same.
     *
     * @see IsActualEqualToExpectedWithEmptyFailMessageValueComparer
     */
    public static final ValueComparator isActualEqualToExpectedWithEmptyFailMessage = new IsActualEqualToExpectedWithEmptyFailMessageValueComparer();

    /**
     * Ignores milliseconds as not all databases store it in Timestamp.
     *
     * @see IsActualWithinToleranceOfExpectedTimestampValueComparer
     */
    public static final ValueComparator isActualEqualToExpectedTimestampWithIgnoreMillis = new IsActualWithinToleranceOfExpectedTimestampValueComparer(0, ONE_SECOND_IN_MILLIS);

    /**
     * @see IsActualNotEqualToExpectedValueComparer
     */
    public static final ValueComparator isActualNotEqualToExpected = new IsActualNotEqualToExpectedValueComparer();

    /**
     * @see IsActualGreaterThanExpectedValueComparer
     */
    public static final ValueComparator isActualGreaterThanExpected = new IsActualGreaterThanExpectedValueComparer();

    /**
     * @see IsActualGreaterThanOrEqualToExpectedValueComparer
     */
    public static final ValueComparator isActualGreaterThanOrEqualToExpected = new IsActualGreaterThanOrEqualToExpectedValueComparer();

    /**
     * @see IsActualLessThanOrEqualToExpectedValueComparer
     */
    public static final ValueComparator isActualLessOrEqualToThanExpected = new IsActualLessThanOrEqualToExpectedValueComparer();

    /**
     * @see IsActualLessThanExpectedValueComparer
     */
    public static final ValueComparator isActualLessThanExpected = new IsActualLessThanExpectedValueComparer();

    /**
     * @see IsActualWithinToleranceOfExpectedTimestampValueComparer
     */
    public static final ValueComparator isActualWithinOneSecondNewerOfExpectedTimestamp = new IsActualWithinToleranceOfExpectedTimestampValueComparer(0, ONE_SECOND_IN_MILLIS);

    /**
     * @see IsActualWithinToleranceOfExpectedTimestampValueComparer
     */
    public static final ValueComparator isActualWithinOneSecondOlderOfExpectedTimestamp = new IsActualWithinToleranceOfExpectedTimestampValueComparer(ONE_SECOND_IN_MILLIS, 0);

    /**
     * @see IsActualWithinToleranceOfExpectedTimestampValueComparer
     */
    public static final ValueComparator isActualWithinOneMinuteNewerOfExpectedTimestamp = new IsActualWithinToleranceOfExpectedTimestampValueComparer(0, ONE_MINUTE_IN_MILLIS);

    /**
     * @see IsActualWithinToleranceOfExpectedTimestampValueComparer
     */
    public static final ValueComparator isActualWithinOneMinuteOlderOfExpectedTimestamp = new IsActualWithinToleranceOfExpectedTimestampValueComparer(ONE_MINUTE_IN_MILLIS, 0);

    /**
     * @see IsActualContainingExpectedStringValueComparer
     * @since 2.7.0
     */
    public static final ValueComparator IS_ACTUAL_CONTAINING_EXPECTED_STRING_VALUE_COMPARATOR = new IsActualContainingExpectedStringValueComparer();

    /**
     * @see NeverFailsValueComparer
     */
    public static final ValueComparator neverFails = new NeverFailsValueComparer();
}