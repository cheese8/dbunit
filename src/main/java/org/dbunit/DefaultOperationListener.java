/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2009, DbUnit.org
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

import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.database.IDatabaseConnection;

/**
 * Default implementation of the {@link IOperationListener}.
 *
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.4
 */
@Slf4j
public class DefaultOperationListener implements IOperationListener {
    public void connectionRetrieved(IDatabaseConnection connection) {
        log.debug("connectionCreated(connection={}) - start", connection);
        // Is by default a no-op
    }

    public void operationSetUpFinished(IDatabaseConnection connection) {
        log.debug("operationSetUpFinished(connection={}) - start", connection);
        closeConnection(connection);
    }

    public void operationTearDownFinished(IDatabaseConnection connection) {
        log.debug("operationTearDownFinished(connection={}) - start", connection);
        closeConnection(connection);
    }

    private void closeConnection(IDatabaseConnection connection) {
        log.debug("closeConnection(connection={}) - start", connection);
        try {
            connection.close();
        } catch (SQLException e) {
            log.warn("Exception while closing the connection: " + e, e);
        }
    }
}