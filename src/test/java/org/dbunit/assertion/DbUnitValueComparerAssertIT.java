package org.dbunit.assertion;

import java.io.FileNotFoundException;
import java.util.Map;

import org.dbunit.assertion.comparer.value.ValueComparator;
import org.dbunit.assertion.comparer.value.ValueComparers;
import org.dbunit.assertion.comparer.value.builder.ColumnValueComparatorMapBuilder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.testutil.TestUtils;
import org.junit.Test;

public class DbUnitValueComparerAssertIT {
    public static final String FILE_PATH = "xml/assertionTest.xml";

    private final DbUnitValueComparerAssert sut = new DbUnitValueComparerAssert();

    private IDataSet getDataSet() throws DataSetException, FileNotFoundException {
        return new FlatXmlDataSetBuilder().build(TestUtils.getFileReader(FILE_PATH), null);
    }

    @Test
    public void testAssertWithValueComparerITableITableValueComparer_AllRowsEqual_NoFail() throws Exception {
        final IDataSet dataSet = getDataSet();
        final ITable expectedTable = dataSet.getTable("TEST_TABLE");
        final ITable actualTable = dataSet.getTable("TEST_TABLE_WITH_SAME_VALUE");
        final ValueComparator defaultValueComparator = ValueComparers.isActualEqualToExpected;
        sut.assertWithValueComparer(expectedTable, actualTable, defaultValueComparator);
    }

    @Test
    public void testAssertWithValueComparerITableITableValueComparerMap_OneColumnNotEqual_NoFail() throws Exception {
        final IDataSet dataSet = getDataSet();
        final ITable expectedTable = dataSet.getTable("TEST_TABLE");
        final ITable actualTable = dataSet.getTable("TEST_TABLE_WITH_WRONG_VALUE_ALL_ROWS");
        final ValueComparator defaultValueComparator = ValueComparers.isActualEqualToExpected;
        final ValueComparator valueComparator = ValueComparers.isActualNotEqualToExpected;
        final Map<String, ValueComparator> columnValueComparers = new ColumnValueComparatorMapBuilder().add("COLUMN2", valueComparator).build();
        sut.assertWithValueComparer(expectedTable, actualTable, defaultValueComparator, columnValueComparers);
    }
}
