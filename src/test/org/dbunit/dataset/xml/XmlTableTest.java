/*
 * XmlTableTest.java   Feb 17, 2002
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

package org.dbunit.dataset.xml;

import org.dbunit.dataset.*;

import java.io.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class XmlTableTest extends AbstractTableTest
{
    public XmlTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet().getTable("TEST_TABLE");
    }

    protected IDataSet createDataSet() throws Exception
    {
        InputStream in = new FileInputStream(
                new File("src/xml/xmlTableTest.xml"));
        return new XmlDataSet(in);
    }

    public void testGetMissingValue() throws Exception
    {
        Object[] expected = {null, ITable.NO_VALUE, "value", "", "   ", ITable.NO_VALUE};

        ITable table = createDataSet().getTable("MISSING_AND_NULL_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("value " + i, expected[i],
                    table.getValue(0, columns[i].getColumnName()));
        }
    }

}





