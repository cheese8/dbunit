package org.dbunit.util;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.database.IDatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class ResourceUtil {
    public static void release(IDatabaseConnection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void releaseSilently(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("releaseSilently {}", connection, e);
        }
    }
}