package org.dbunit.ant;

import lombok.Getter;
import lombok.Setter;

public class Feature {
    @Getter
    @Setter
    private String name;
    private boolean value;
    public boolean isValue() {
        return value;
    }
    public void setValue(boolean value) {
        this.value = value;
    }
}