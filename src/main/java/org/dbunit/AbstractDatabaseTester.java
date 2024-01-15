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

package org.dbunit;

import lombok.Getter;
import lombok.Setter;
import org.dbunit.assertion.DefaultFailureHandler;
import org.dbunit.assertion.SimpleAssert;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.Assert;

/**
 * Basic implementation of IDatabaseTester.<br>
 * Implementations of IDatabaseTester may use this class as a starting point.
 *
 * @author Andres Almiray (aalmiray@users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.0
 */
public abstract class AbstractDatabaseTester extends SimpleAssert implements IDatabaseTester {
    /**
     * Enumeration of the valid {@link OperationType}s
     */
    private static final class OperationType {
        public static final OperationType SET_UP = new OperationType("setUp");
        public static final OperationType TEAR_DOWN = new OperationType("tearDown");

        private final String key;

        private OperationType(String key)
        {
            this.key = key;
        }

        @Override
        public String toString()
        {
            return "OperationType: " + key;
        }
    }

    @Getter @Setter
    private IDataSet dataSet;
    @Getter @Setter
    private String schema;
    @Getter @Setter
    private DatabaseOperation setUpOperation = DatabaseOperation.CLEAN_INSERT;
    @Getter @Setter
    private DatabaseOperation tearDownOperation = DatabaseOperation.NONE;
    @Setter
    private IOperationListener operationListener;

    public AbstractDatabaseTester() {
        this(null);
    }

    /**
     * @param schema
     *            The schema to be tested. Can be <code>null</code>
     * @since 2.4.3
     */
    public AbstractDatabaseTester(String schema) {
        super(new DefaultFailureHandler());
        this.schema = schema;
    }

    public void closeConnection(IDatabaseConnection connection) throws Exception {
        connection.close();
    }

    public void onSetup() throws Exception {
        executeOperation(getSetUpOperation(), OperationType.SET_UP);
    }

    public void onTearDown() throws Exception {
        executeOperation(getTearDownOperation(), OperationType.TEAR_DOWN);
    }

    /**
     * Executes a DatabaseOperation with a IDatabaseConnection supplied by
     * {@link #getConnection()} and the test dataset.
     */
    private void executeOperation(DatabaseOperation operation, OperationType type) throws Exception {
        Assert.assertThat(type == OperationType.SET_UP || type == OperationType.TEAR_DOWN, new DatabaseUnitRuntimeException("Cannot happen - unknown OperationType specified: " + type));
        if (operation == DatabaseOperation.NONE) {
            return;
        }
        // Ensure that the operationListener is set
        if (operationListener == null) {
            operationListener = new DefaultOperationListener();
        }
        IDatabaseConnection connection = getConnection();
        operationListener.connectionRetrieved(connection);
        try {
            operation.execute(connection, getDataSet());
        } finally {
            // Since 2.4.4 the OperationListener is responsible for closing
            // the connection at the right time
            if (type == OperationType.SET_UP) {
                operationListener.operationSetUpFinished(connection);
            } else if (type == OperationType.TEAR_DOWN) {
                operationListener.operationTearDownFinished(connection);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + "schema=" + schema + ", dataSet=" + dataSet + ", setUpOperation=" + setUpOperation + ", tearDownOperation=" + tearDownOperation + ", operationListener=" + operationListener + "]";
    }
}