package org.dbunit.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Future {

    /**
     * Flag for using the qualified table names.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean useQualifiedTableNames = null;

    /**
     * Flag for using batched statements.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean supportBatchStatement = null;

    /**
     * Flag for datatype warning.
     *
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean datatypeWarning = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private String escapePattern = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private String dataTypeFactory = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Getter @Setter
    private String batchSize = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Getter @Setter
    private String fetchSize = null;

    /**
     * @deprecated since 2.4. Use {@link #dbConfig} instead. Only here because of backwards compatibility should be removed in the next major release.
     */
    @Setter
    private Boolean skipOracleRecycleBinTables = null;
}
