package org.dbunit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public final class JdbcConfig {
    private String driverClass;
    private String url;
    private String schema;
    private String userId;
    private String password;
}