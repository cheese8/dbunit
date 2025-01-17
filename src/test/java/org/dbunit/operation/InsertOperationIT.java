/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.operation;

import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.Assertion;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.TestFeature;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ForwardOnlyDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class InsertOperationIT extends AbstractDatabaseIT {
    public InsertOperationIT(String s) {
        super(s);
    }

    public void testMockExecute() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                "insert into schema.table (c1, c2, c3) values ('toto', 1234, 'false')",
                "insert into schema.table (c1, c2, c3) values ('qwerty', 123.45, 'true')",
        };

        // setup table
        Column[] columns = new Column[]{
                new Column("c1", DataType.VARCHAR),
                new Column("c2", DataType.NUMERIC),
                new Column("c3", DataType.BOOLEAN),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"toto", "1234", Boolean.FALSE});
        table.addRow(new Object[]{"qwerty", new Double("123.45"), "true"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithBlanksDisabledAndEmptyString() throws Exception {
        String schemaName = "schema";
        String tableName = "table";

        Column[] columns = new Column[]{
                new Column("c3", DataType.VARCHAR),
                new Column("c4", DataType.NUMERIC),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"", "1"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.setExpectedExecuteBatchCalls(0);
        statement.setExpectedClearBatchCalls(0);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        connection.getConfig().setFeature(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, false);
        try {
            new InsertOperation().execute(connection, dataSet);
            fail("Update should not succedd");
        } catch (IllegalArgumentException e) {
            // ignore
        } finally {
            statement.verify();
            factory.verify();
            connection.verify();
        }
    }

    public void testExecuteWithBlanksDisabledAndNonEmptyStrings() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                String.format("insert into %s.%s (c3, c4) values ('not-empty', 1)", schemaName, tableName),
                String.format("insert into %s.%s (c3, c4) values (NULL, 2)", schemaName, tableName)
        };

        Column[] columns = new Column[]{
                new Column("c3", DataType.VARCHAR),
                new Column("c4", DataType.NUMERIC),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"not-empty", "1"});
        table.addRow(new Object[]{null, "2"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        connection.getConfig().setFeature(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, false);
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithBlanksAllowed() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                String.format("insert into %s.%s (c3, c4) values ('not-empty', 1)", schemaName, tableName),
                String.format("insert into %s.%s (c3, c4) values (NULL, 2)", schemaName, tableName),
                String.format("insert into %s.%s (c3, c4) values ('', 3)", schemaName, tableName),
        };

        Column[] columns = new Column[]{
                new Column("c3", DataType.VARCHAR),
                new Column("c4", DataType.NUMERIC),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"not-empty", "1"});
        table.addRow(new Object[]{null, "2"});
        table.addRow(new Object[]{"", "3"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        connection.getConfig().setFeature(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteUnknownColumn() throws Exception {
        String tableName = "table";

        // setup table
        Column[] columns = new Column[]{
                new Column("column", DataType.VARCHAR),
                new Column("unknown", DataType.VARCHAR),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow();
        table.setValue(0, columns[0].getColumnName(), null);
        table.setValue(0, columns[0].getColumnName(), "value");
        IDataSet insertDataset = new DefaultDataSet(table);

        IDataSet databaseDataSet = new DefaultDataSet(
                new DefaultTable(tableName, new Column[]{
                        new Column("column", DataType.VARCHAR),
                }));

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.setExpectedExecuteBatchCalls(0);
        statement.setExpectedClearBatchCalls(0);
        statement.setExpectedCloseCalls(0);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(0);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(databaseDataSet);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        try {
            new InsertOperation().execute(connection, insertDataset);
            fail("Should not be here!");
        } catch (NoSuchColumnException e) {

        }

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteIgnoreNone() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                "insert into schema.table (c1, c2, c3) values ('toto', 1234, 'false')",
                "insert into schema.table (c2, c3) values (123.45, 'true')",
                "insert into schema.table (c1, c2, c3) values ('qwerty1', 1, 'true')",
                "insert into schema.table (c1, c2, c3) values ('qwerty2', 2, 'false')",
                "insert into schema.table (c3) values ('false')",
        };

        // setup table
        Column[] columns = new Column[]{
                new Column("c1", DataType.VARCHAR),
                new Column("c2", DataType.NUMERIC),
                new Column("c3", DataType.BOOLEAN),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"toto", "1234", Boolean.FALSE});
        table.addRow(new Object[]{ITable.NO_VALUE, new Double("123.45"), "true"});
        table.addRow(new Object[]{"qwerty1", "1", Boolean.TRUE});
        table.addRow(new Object[]{"qwerty2", "2", Boolean.FALSE});
        table.addRow(new Object[]{ITable.NO_VALUE, ITable.NO_VALUE, Boolean.FALSE});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(4);
        statement.setExpectedClearBatchCalls(4);
        statement.setExpectedCloseCalls(4);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(4);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

//    public void testExecuteNullAsNone() throws Exception
//    {
//        String schemaName = "schema";
//        String tableName = "table";
//        String[] expected = {
//            "insert into schema.table (c1, c2, c3) values ('toto', 1234, 'false')",
//            "insert into schema.table (c2, c3) values (123.45, 'true')",
//            "insert into schema.table (c1, c2, c3) values ('qwerty1', 1, 'true')",
//            "insert into schema.table (c1, c2, c3) values ('qwerty2', 2, 'false')",
//            "insert into schema.table (c3) values ('false')",
//        };
//
//        // setup table
//        List valueList = new ArrayList();
//        valueList.add(new Object[]{"toto", "1234", Boolean.FALSE});
//        valueList.add(new Object[]{null, new Double("123.45"), "true"});
//        valueList.add(new Object[]{"qwerty1", "1", Boolean.TRUE});
//        valueList.add(new Object[]{"qwerty2", "2", Boolean.FALSE});
//        valueList.add(new Object[]{null, null, Boolean.FALSE});
//        Column[] columns = new Column[]{
//            new Column("c1", DataType.VARCHAR),
//            new Column("c2", DataType.NUMERIC),
//            new Column("c3", DataType.BOOLEAN),
//        };
//        DefaultTable table = new DefaultTable(tableName, columns, valueList);
//        IDataSet dataSet = new DefaultDataSet(table);
//
//        // setup mock objects
//        MockBatchStatement statement = new MockBatchStatement();
//        statement.addExpectedBatchStrings(expected);
//        statement.setExpectedExecuteBatchCalls(4);
//        statement.setExpectedClearBatchCalls(4);
//        statement.setExpectedCloseCalls(4);
//
//        MockStatementFactory factory = new MockStatementFactory();
//        factory.setExpectedCreatePreparedStatementCalls(4);
//        factory.setupStatement(statement);
//
//        MockDatabaseConnection connection = new MockDatabaseConnection();
//        connection.setupDataSet(dataSet);
//        connection.setupSchema(schemaName);
//        connection.setupStatementFactory(factory);
//        connection.setExpectedCloseCalls(0);
//        DatabaseConfig config = connection.getConfig();
//        config.setFeature(DatabaseConfig.FEATURE_NULL_AS_NONE, true);
//
//        // execute operation
//        new InsertOperation().execute(connection, dataSet);
//
//        statement.verify();
//        factory.verify();
//        connection.verify();
//    }

    public void testExecuteWithEscapedNames() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                "insert into 'schema'.'table' ('c1', 'c2', 'c3') values ('toto', 1234, 'false')",
                "insert into 'schema'.'table' ('c1', 'c2', 'c3') values ('qwerty', 123.45, 'true')",
        };

        // setup table
        Column[] columns = new Column[]{
                new Column("c1", DataType.VARCHAR),
                new Column("c2", DataType.NUMERIC),
                new Column("c3", DataType.BOOLEAN),
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{"toto", "1234", Boolean.FALSE});
        table.addRow(new Object[]{"qwerty", new Double("123.45"), "true"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        connection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "'?'");
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEmptyTable() throws Exception {
        Column[] columns = {new Column("c1", DataType.VARCHAR)};
        ITable table = new DefaultTable(new DefaultTableMetaData(
                "name", columns, columns));
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(0);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new InsertOperation().execute(connection, dataSet);

        factory.verify();
        connection.verify();
    }

    public void testInsertClob() throws Exception {
        // execute this test only if the target database support CLOB
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.CLOB)) {
            String tableName = "CLOB_TABLE";

            Reader in = new FileReader(TestUtils.getFile("xml/clobInsertTest.xml"));
            IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(in, null);

            assertEquals("count before", 0, connection.getRowCount(tableName));

            DatabaseOperation.INSERT.execute(connection, xmlDataSet);

            ITable tableAfter = connection.createDataSet().getTable(tableName);
            assertEquals("count after", 3, tableAfter.getRowCount());
            Assertion.assertEquals(xmlDataSet.getTable(tableName), tableAfter);
        }
    }

    public void testInsertBlob() throws Exception {
        // execute this test only if the target database support BLOB
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.BLOB)) {
            String tableName = "BLOB_TABLE";

            Reader in = new FileReader(TestUtils.getFile("xml/blobInsertTest.xml"));
            IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(in, null);

            assertEquals("count before", 0, connection.getRowCount(tableName));

            DatabaseOperation.INSERT.execute(connection, xmlDataSet);

            ITable tableAfter = connection.createDataSet().getTable(tableName);
            assertEquals("count after", 3, tableAfter.getRowCount());
            Assertion.assertEquals(xmlDataSet.getTable(tableName), tableAfter);
        }
    }

    public void testInsertSdoGeometry() throws Exception {
        // execute this test only if the target database supports SDO_GEOMETRY
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.SDO_GEOMETRY)) {
            String tableName = "SDO_GEOMETRY_TABLE";

            Reader in = new FileReader(TestUtils.getFile("xml/sdoGeometryInsertTest.xml"));
            IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(in, null);

            assertEquals("count before", 0, connection.getRowCount(tableName));

            DatabaseOperation.INSERT.execute(connection, xmlDataSet);

            ITable tableAfter = connection.createDataSet().getTable(tableName);
            assertEquals("count after", 1, tableAfter.getRowCount());
            Assertion.assertEquals(xmlDataSet.getTable(tableName), tableAfter);
        }
    }

    public void testInsertXmlType() throws Exception {
        // execute this test only if the target database support CLOB
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.XML_TYPE)) {
            String tableName = "XML_TYPE_TABLE";

            Reader in = new FileReader(TestUtils.getFile("xml/xmlTypeInsertTest.xml"));
            IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(in, null);

            assertEquals("count before", 0, connection.getRowCount(tableName));

            DatabaseOperation.INSERT.execute(connection, xmlDataSet);

            ITable tableAfter = connection.createDataSet().getTable(tableName);
            assertEquals("count after", 3, tableAfter.getRowCount());
            Assertion.assertEquals(xmlDataSet.getTable(tableName), tableAfter);
        }
    }

    public void testMissingColumns() throws Exception {
        Reader in = TestUtils.getFileReader("xml/missingColumnTest.xml");
        IDataSet xmlDataSet = new XmlDataSet(in, null);

        ITable[] tablesBefore = DataSetUtils.getTables(connection.createDataSet());
        DatabaseOperation.INSERT.execute(connection, xmlDataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(connection.createDataSet());

        // verify tables before
        for (int i = 0; i < tablesBefore.length; i++) {
            ITable table = tablesBefore[i];
            String tableName = table.getTableMetaData().getTableName();
            if (tableName.startsWith("EMPTY")) {
                assertEquals(tableName + " before", 0, table.getRowCount());
            }
        }

        // verify tables after
        for (int i = 0; i < tablesAfter.length; i++) {
            ITable databaseTable = tablesAfter[i];
            String tableName = databaseTable.getTableMetaData().getTableName();

            if (tableName.startsWith("EMPTY")) {
                Column[] columns = databaseTable.getTableMetaData().getColumns();
                ITable xmlTable = xmlDataSet.getTable(tableName);

                // verify row count
                assertEquals("row count", xmlTable.getRowCount(),
                        databaseTable.getRowCount());

                // for each table row
                for (int j = 0; j < databaseTable.getRowCount(); j++) {
                    // verify first column values
                    Object expected = xmlTable.getValue(j, columns[0].getColumnName());
                    Object actual = databaseTable.getValue(j, columns[0].getColumnName());

                    assertEquals(tableName + "." + columns[0].getColumnName(),
                            expected, actual);

                    // all remaining columns should be null except mssql server timestamp column which is of type binary.
                    for (int k = 1; k < columns.length; k++) {
                        String columnName = columns[k].getColumnName();
                        assertEquals(tableName + "." + columnName,
                                null, databaseTable.getValue(j, columnName));
                    }
                }
            }
        }

    }

    public void testDefaultValues() throws Exception {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
                "insert into schema.table (c1, c3, c4) values (NULL, NULL, NULL)"
        };

        // setup table
        Column[] columns = new Column[]{
                new Column("c1", DataType.NUMERIC, Column.NO_NULLS), // Disallow null, no default
                new Column("c2", DataType.NUMERIC, DataType.NUMERIC.toString(), Column.NO_NULLS, "2"), // Disallow null, default
                new Column("c3", DataType.NUMERIC, Column.NULLABLE), // Allow null, no default
                new Column("c4", DataType.NUMERIC, DataType.NUMERIC.toString(), Column.NULLABLE, "4"), // Allow null, default
        };
        DefaultTable table = new DefaultTable(tableName, columns);
        table.addRow(new Object[]{null, null, null, null});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new InsertOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecute() throws Exception {
        Reader in = TestUtils.getFileReader("xml/insertOperationTest.xml");
        IDataSet dataSet = new XmlDataSet(in, null);

        testExecute(dataSet);
    }

    public void testExecuteCaseInsensitive() throws Exception {
        Reader in = TestUtils.getFileReader("xml/insertOperationTest.xml");
        IDataSet dataSet = new XmlDataSet(in, null);

        testExecute(new LowerCaseDataSet(dataSet));
    }

    public void testExecuteForwardOnly() throws Exception {
        Reader in = TestUtils.getFileReader("xml/insertOperationTest.xml");
        IDataSet dataSet = new XmlDataSet(in, null);

        testExecute(new ForwardOnlyDataSet(dataSet));
    }

    private void testExecute(IDataSet dataSet) throws Exception, SQLException {
        ITable[] tablesBefore = DataSetUtils.getTables(connection.createDataSet());
        DatabaseOperation.INSERT.execute(connection, dataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(connection.createDataSet());

        assertEquals("table count", tablesBefore.length, tablesAfter.length);
        for (int i = 0; i < tablesBefore.length; i++) {
            ITable table = tablesBefore[i];
            String name = table.getTableMetaData().getTableName();


            if (name.startsWith("EMPTY")) {
                assertEquals(name + "before", 0, table.getRowCount());
            }
        }

        for (int i = 0; i < tablesAfter.length; i++) {
            ITable table = tablesAfter[i];
            String name = table.getTableMetaData().getTableName();

            if (name.startsWith("EMPTY")) {
                if (dataSet instanceof ForwardOnlyDataSet) {
                    assertTrue(name, table.getRowCount() > 0);
                } else {
                    SortedTable expectedTable = new SortedTable(
                            dataSet.getTable(name), dataSet.getTable(name).getTableMetaData());
                    SortedTable actualTable = new SortedTable(table);
                    Assertion.assertEquals(expectedTable, actualTable);
                }
            }
        }
    }
}










