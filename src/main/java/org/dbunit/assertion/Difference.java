/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2008, DbUnit.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.assertion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dbunit.dataset.ITable;

/**
 * Value object to hold the difference of a single data cell found while
 * comparing data.
 * <p>
 * Inspired by the XMLUnit framework.
 * </p>
 *
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 * @since 2.6.0 added failMessage
 */
@AllArgsConstructor
@Getter
public class Difference {
    private ITable expectedTable;
    private ITable actualTable;
    private int rowIndex;
    private String columnName;
    private Object expectedValue;
    private Object actualValue;
    private String failMessage;

    public Difference(final ITable expectedTable, final ITable actualTable, final int rowIndex, final String columnName, final Object expectedValue, final Object actualValue) {
        this(expectedTable, actualTable, rowIndex, columnName, expectedValue, actualValue, "");
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + "expectedTable=" + expectedTable + ", actualTable=" + actualTable + ", rowIndex=" + rowIndex + ", columnName=" + columnName + ", expectedValue=" + expectedValue + ", actualValue=" + actualValue + ", failMessage=" + failMessage + "]";
    }
}