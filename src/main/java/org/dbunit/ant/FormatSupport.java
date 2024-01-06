package org.dbunit.ant;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum FormatSupport {
    FLAT("FLAT"), XML("XML"), DTD("DTD"), CSV("CSV"), XLS("XLS");

    private static Map<String, FormatSupport> map = new HashMap<>();

    static {
        for (FormatSupport format : FormatSupport.values()) {
            map.put(format.getFormat(), format);
        }
    }

    public static FormatSupport of(String format) {
        return map.get(format);
    }

    @Getter
    private String format;

    private FormatSupport(String format) {
        this.format = format;
    }
}