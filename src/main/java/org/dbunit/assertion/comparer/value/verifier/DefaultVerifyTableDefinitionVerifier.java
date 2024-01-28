package org.dbunit.assertion.comparer.value.verifier;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.VerifyTableDefinition;
import org.dbunit.assertion.comparer.value.ValueComparator;

/**
 * Default implementation for {@link VerifyTableDefinitionVerifier} which throws {@link IllegalStateException} on configuration conflicts.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
@Slf4j
public class DefaultVerifyTableDefinitionVerifier implements VerifyTableDefinitionVerifier {
    @Override
    public void verify(final VerifyTableDefinition verifyTableDefinition) {
        final String tableName = verifyTableDefinition.getTableName();
        final String[] columnExclusionFilters = verifyTableDefinition.getColumnExclusionFilters();
        final Map<String, ValueComparator> columnValueComparators = verifyTableDefinition.getColumnValueComparators();
        verify(tableName, columnExclusionFilters, columnValueComparators);
    }

    public void verify(final String tableName, final String[] columnExclusionFilters, final Map<String, ValueComparator> columnValueComparers) {
        final boolean hasColumnExclusionFilters = hasColumnExclusionFilters(columnExclusionFilters);
        final boolean hasColumnValueComparers = hasColumnValueComparers(columnValueComparers);
        if (hasColumnExclusionFilters && hasColumnValueComparers) {
            doVerify(tableName, columnExclusionFilters, columnValueComparers);
        }
    }

    /**
     * Verify the columnExclusionFilters and columnValueComparers agree.
     */
    protected void doVerify(final String tableName, final String[] columnExclusionFilters, final Map<String, ValueComparator> columnValueComparers) {
        for (final String columnName : columnExclusionFilters) {
            log.trace("doVerify: columnName={}", columnName);
            failIfColumnValueComparersHaveExcludedColumn(tableName, columnName, columnValueComparers);
        }
    }

    protected void failIfColumnValueComparersHaveExcludedColumn(final String tableName, final String columnName, final Map<String, ValueComparator> columnValueComparers) {
        final ValueComparator valueComparator = columnValueComparers.get(columnName);
        if (valueComparator == null) {
            log.trace("failIfColumnValueComparersHaveExcludedColumn: config ok as no valueComparer found for excluded columnName={}", columnName);
        } else {
            final String msg = String.format("Test setup conflict: table=%s, columnName=%s, has a VerifyTableDefinition column exclusion and a specific column ValueComparer=%s, " +
                    "to test the column, remove the exclusion to ignore the column, remove the ValueComparer", tableName, columnName, valueComparator);
            log.error("failIfColumnValueComparersHaveExcludedColumn: {}", msg);
            throw new IllegalStateException(msg);
        }
    }

    protected boolean hasColumnExclusionFilters(final String[] columnExclusionFilters) {
        final boolean isMissing = columnExclusionFilters == null || columnExclusionFilters.length == 0;
        if (isMissing) {
            log.info("hasColumnExclusionFilters: no columnExclusionFilters specified");
        }
        return !isMissing;
    }

    protected boolean hasColumnValueComparers(final Map<String, ValueComparator> columnValueComparers) {
        final boolean isMissing = columnValueComparers == null || columnValueComparers.isEmpty();
        if (isMissing) {
            log.info("hasColumnValueComparers: no columnValueComparers specified");
        }
        return !isMissing;
    }
}