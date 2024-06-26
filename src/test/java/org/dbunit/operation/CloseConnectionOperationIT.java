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

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 6, 2002
 */
public class CloseConnectionOperationIT extends AbstractDatabaseIT {
    public CloseConnectionOperationIT(String s) {
        super(s);
    }

    public void testMockExecute() throws Exception {
        // setup mock objects
        MockDatabaseOperation operation = new MockDatabaseOperation();
        operation.setExpectedExecuteCalls(1);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setExpectedCloseCalls(1);

        // execute operation
        new CloseConnectionOperation(operation).execute(connection, (IDataSet)null);

        // verify
        operation.verify();
        connection.verify();
    }

}




