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

package org.dbunit.database;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class adapts a JDBC <code>DataSource</code> to a
 * {@link IDatabaseConnection}.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 8, 2002
 */
public class DatabaseDataSourceConnection extends AbstractDatabaseConnection implements IDatabaseConnection {
    private final String schema;
    private final DataSource dataSource;
    private final String user;
    private final String password;
    private Connection connection;

    public DatabaseDataSourceConnection(InitialContext context, String jndiName, String schema) throws NamingException {
        this((DataSource) context.lookup(jndiName), schema, null, null);
    }

    public DatabaseDataSourceConnection(InitialContext context, String jndiName, String schema, String user, String password) throws NamingException {
        this((DataSource) context.lookup(jndiName), schema, user, password);
    }

    public DatabaseDataSourceConnection(InitialContext context, String jndiName) throws NamingException {
        this(context, jndiName, null);
    }

    public DatabaseDataSourceConnection(InitialContext context, String jndiName, String user, String password) throws NamingException {
        this(context, jndiName, null, user, password);
    }

    public DatabaseDataSourceConnection(DataSource dataSource) {
        this(dataSource, null, null, null);
    }

    public DatabaseDataSourceConnection(DataSource dataSource, String user, String password) {
        this(dataSource, null, user, password);
    }

    public DatabaseDataSourceConnection(DataSource dataSource, String schema) {
        this(dataSource, schema, null, null);
    }

    public DatabaseDataSourceConnection(DataSource dataSource, String schema, String user, String password) {
        this.dataSource = dataSource;
        this.schema = schema;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            if (user != null) {
                connection = dataSource.getConnection(user, password);
            } else {
                connection = dataSource.getConnection();
            }
        }
        return connection;
    }

    public String getSchema() {
        return schema;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}