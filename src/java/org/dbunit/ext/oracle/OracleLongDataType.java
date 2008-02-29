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
 
 package org.dbunit.ext.oracle;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.StringDataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

    public class OracleLongDataType extends StringDataType {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OracleLongDataType.class);

        public OracleLongDataType() {
            super("LONGVARCHAR", Types.LONGVARCHAR);
        }

        public void setSqlValue(Object value, int column,
                PreparedStatement statement) throws SQLException,
                TypeCastException {
                	
        logger.debug("setSqlValue(value=" + value + ", column=" + column + ", statement=" + statement + ") - start");
            String s = DataType.asString(value);
            statement.setCharacterStream(column, new StringReader(s),
                    s.length());
        }
    }