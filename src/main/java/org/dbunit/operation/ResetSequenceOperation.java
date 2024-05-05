package org.dbunit.operation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

import java.sql.SQLException;
import java.sql.Statement;

public class ResetSequenceOperation extends DatabaseOperation {

    private final static String RESET_SEQ_MYSQL = "";
    private final static String RESET_SEQ_PG = "";
    private final static String RESET_SEQ_ORACLE = "";

    @Override
    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        String[] tables = dataSet.getTableNames();
        DatabaseConfig databaseConfig = connection.getConfig();
        String databaseType = String.valueOf(databaseConfig.getProperty(DatabaseConfig.PROPERTY_DATABASE_TYPE));
        Statement statement = connection.getConnection().createStatement();
        for (String table : tables) {
            int startWith = dataSet.getTable(table).getRowCount() + 1;
            statement.execute("alter sequence " + table + "_PK_SEQ RESTART WITH "+ startWith);

        }
    }
}