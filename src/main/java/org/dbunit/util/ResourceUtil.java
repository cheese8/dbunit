package org.dbunit.util;

import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;

public class ResourceUtil {
    public static void release(IDatabaseConnection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}