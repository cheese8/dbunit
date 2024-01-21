package org.dbunit.util;

public class Assert {
    public final static void assertThat(Boolean condition, RuntimeException throwable) {
        if (!condition) {
            throw throwable;
        }
    }
}
