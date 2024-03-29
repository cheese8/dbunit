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

import java.sql.Types;

import org.dbunit.dataset.datatype.BinaryStreamDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.StringDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialized factory that recognizes Oracle data types for Oracle 10 and higher.
 * <br>
 * Handles the CLOBs and BLOBs as string and binary stream respectively which is supported
 * since oracle 10.
 * <br>
 * This is recommended by oracle:
 * <a href="http://www.oracle.com/technology/sample_code/tech/java/codesnippet/jdbc/clob10g/handlingclobsinoraclejdbc10g.html">
 * Oracle technology sample code</a>
 *
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class Oracle10DataTypeFactory extends OracleDataTypeFactory {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Oracle10DataTypeFactory.class);


    protected static final DataType CLOB_AS_STRING = new StringDataType("CLOB", Types.CLOB);
    protected static final DataType BLOB_AS_STREAM = new BinaryStreamDataType("BLOB", Types.BLOB);

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (logger.isDebugEnabled())
            logger.debug("createDataType(sqlType={}, sqlTypeName={}) - start", String.valueOf(sqlType), sqlTypeName);

        // BLOB
        if ("BLOB".equals(sqlTypeName)) {
            return BLOB_AS_STREAM;
        }

        // CLOB
        if ("CLOB".equals(sqlTypeName)) {
            return CLOB_AS_STRING;
        }
        return super.createDataType(sqlType, sqlTypeName);
    }
}
