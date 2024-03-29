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

package org.dbunit.dataset.datatype;

import org.dbunit.database.ExtendedMockSingleRowResultSet;
import org.dbunit.dataset.ITable;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Clock;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */

public class TimeDataTypeTest extends AbstractDataTypeTest {
    private final static DataType THIS_TYPE = DataType.TIME;

    public TimeDataTypeTest(String name) {
        super(name);
    }

    public void testToString() throws Exception {
        assertEquals("name", "TIME", THIS_TYPE.toString());
    }

    public void testGetTypeClass() throws Exception {
        assertEquals("class", Time.class, THIS_TYPE.getTypeClass());
    }

    public void testIsNumber() throws Exception {
        assertEquals("is number", false, THIS_TYPE.isNumber());
    }

    public void testIsDateTime() throws Exception {
        assertEquals("is date/time", true, THIS_TYPE.isDateTime());
    }

    public void testTypeCast() throws Exception {
        Object[] values = {
                null,
                new Time(1234),
                new java.sql.Date(1234),
                new Timestamp(1234),
                new Time(1234).toString(),
                new java.util.Date(1234),
        };

        java.sql.Time[] expected = {
                null,
                new Time(1234),
                new Time(new java.sql.Date(1234).getTime()),
                new Time(new Timestamp(1234).getTime()),
                Time.valueOf(new Time(1234).toString()),
                new Time(1234),
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++) {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    public void testTypeCastNone() throws Exception {
        assertEquals("typecast", null, THIS_TYPE.typeCast(ITable.NO_VALUE));
    }

    public void testTypeCastInvalid() throws Exception {
        Object[] values = {
                new Integer(1234),
                new Object(),
                "bla",
                "2000.05.05",
        };

        for (int i = 0; i < values.length; i++) {
            try {
                THIS_TYPE.typeCast(values[i]);
                fail("Should throw TypeCastException - " + i);
            } catch (TypeCastException e) {
            }
        }
    }

    public void testTypeCastRelative() throws Exception {
        // @formatter:off
        Object[] values = {
                "[now]",
                "[NOW +1h]",
                "[Now -3m -2h]",
                "[NOW+5s]",
        };

        Clock clock = DataType.RELATIVE_DATE_TIME_PARSER.getClock();

        LocalTime now = LocalTime.now(clock);
        Time[] expected = {
                Time.valueOf(now),
                Time.valueOf(now.plus(1, ChronoUnit.HOURS)),
                Time.valueOf(now.plus(-3, ChronoUnit.MINUTES).plus(-2, ChronoUnit.HOURS)),
                Time.valueOf(now.plus(5, ChronoUnit.SECONDS)),
        };
        // @formatter:on

        assertEquals("actual vs expected count", values.length,
                expected.length);

        // Create a new instance to test relative date/time.
        TimeDataType thisType = new TimeDataType();
        for (int i = 0; i < values.length; i++) {
            assertEquals("typecast " + i, expected[i],
                    thisType.typeCast(values[i]));
        }
    }

    public void testCompareEquals() throws Exception {
        Object[] values1 = {
                null,
                new Time(1234),
                new java.sql.Date(1234),
                new Timestamp(1234),
                new Time(1234).toString(),
                new java.util.Date(1234),
                "00:01:02",
        };

        Object[] values2 = {
                null,
                new Time(1234),
                new Time(new java.sql.Date(1234).getTime()),
                new Time(new Timestamp(1234).getTime()),
                Time.valueOf(new Time(1234).toString()),
                new Time(1234),
                new Time(0, 1, 2),
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++) {
            assertEquals("compare1 " + i, 0, THIS_TYPE.compare(values1[i], values2[i]));
            assertEquals("compare2 " + i, 0, THIS_TYPE.compare(values2[i], values1[i]));
        }
    }

    public void testCompareInvalid() throws Exception {
        Object[] values1 = {
                new Integer(1234),
                new Object(),
                "bla",
                "2000.05.05",
        };
        Object[] values2 = {
                null,
                null,
                null,
                null,
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++) {
            try {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
            } catch (TypeCastException e) {
            }

            try {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
            } catch (TypeCastException e) {
            }
        }
    }

    public void testCompareDifferent() throws Exception {
        Object[] less = {
                null,
                new java.sql.Time(0),
                "08:00:00",
                "08:00:00",
        };

        Object[] greater = {
                new java.sql.Time(1234),
                new java.sql.Time(System.currentTimeMillis()),
                "20:00:00",
                "08:00:01",
        };

        assertEquals("values count", less.length, greater.length);

        for (int i = 0; i < less.length; i++) {
            assertTrue("less " + i, THIS_TYPE.compare(less[i], greater[i]) < 0);
            assertTrue("greater " + i, THIS_TYPE.compare(greater[i], less[i]) > 0);
        }
    }

    public void testSqlType() throws Exception {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.TIME));
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals(Types.TIME, THIS_TYPE.getSqlType());
    }

    /**
     *
     */
    public void testForObject() throws Exception {
        assertEquals(THIS_TYPE, DataType.forObject(new Time(1234)));
    }

    public void testAsString() throws Exception {
        java.sql.Time[] values = {
                new java.sql.Time(1234),
        };

        String[] expected = {
                new java.sql.Time(1234).toString(),
        };


        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++) {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception {
        java.sql.Time[] expected = {
                null,
                new Time(1234),
                new Time(new java.sql.Date(1234).getTime()),
                new Time(new Timestamp(1234).getTime()),
                Time.valueOf(new Time(1234).toString()),
                new Time(1234),
        };

        ExtendedMockSingleRowResultSet resultSet = new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expected);

        for (int i = 0; i < expected.length; i++) {
            Object expectedValue = expected[i];
            Object actualValue = THIS_TYPE.getSqlValue(i + 1, resultSet);
            assertEquals("value", expectedValue, actualValue);
        }
    }

}
