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

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.junit.Assert;

/**
 * DatabaseTester that uses JDBC's Driver Manager to create connections.<br>
 *
 * @author Andres Almiray (aalmiray@users.sourceforge.net)
 * @author Felipe Leme (dbunit@felipeal.net)
 * @version $Revision$
 * @since 2.2
 */
@AllArgsConstructor
public class JdbcDatabaseTester extends AbstractDatabaseTester {
    private final String driverClass;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Creates a new JdbcDatabaseTester with the specified properties.<br>
     * Username and Password are set to null.
     *
     * @param driverClass the classname of the JDBC driver to use
     * @param url the connection url
     */
    public JdbcDatabaseTester(String driverClass, String url) {
        this(driverClass, url, null, null);
    }

    /**
     * Creates a new JdbcDatabaseTester with the specified properties.
     *
     * @param driverClass the classname of the JDBC driver to use
     * @param url the connection url
     * @param username a username that can has access to the database - can be <code>null</code>
     * @param password the user's password - can be <code>null</code>
     * @param schema the database schema to be tested - can be <code>null</code>
     * @throws ClassNotFoundException If the given <code>driverClass</code> was not found
     * @since 2.4.3
     */
    public JdbcDatabaseTester(String driverClass, String url, String username, String password, String schema) throws ClassNotFoundException {
        super(schema);
        this.driverClass = driverClass;
        this.url = url;
        this.username = username;
        this.password = password;
        Assert.assertTrue("driverClass was not specified.", StringUtils.isNotBlank(driverClass));
        Class.forName(driverClass);
    }

    public IDatabaseConnection getConnection() throws Exception {
        Assert.assertTrue("url was not specified.", StringUtils.isNotBlank(url));
        Connection conn;
        if (StringUtils.isAllBlank(username, password)) {
            conn = DriverManager.getConnection(url);
        } else {
            conn = DriverManager.getConnection(url, username, password);
        }
        return new DatabaseConnection(conn, getSchema());
    }

    public String toString() {
        return getClass().getName() + "[" + "url=" + this.url + ", driverClass=" + this.driverClass + ", username=" + this.username + ", password=**********" + ", schema=" + getSchema() + "]";
    }
}