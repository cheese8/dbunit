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

/**
 * DatabaseTester that configures a DriverManager from environment properties.<br>
 * This class defines a set of keys for system properties that need to be
 * present in the environment before using it. Example:
 * <xmp>
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
 *             "com.mycompany.myDriver" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
 *             "jdbc:mydb://host/dbname" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
 *             "myuser" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
 *             "mypasswd" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA,
 *             "myschema" );
 * </xmp>
 *
 * @author Andres Almiray(aalmiray@users.sourceforge.net)
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.0
 */
public class PropertiesBasedJdbcDatabaseTester extends JdbcDatabaseTester {
    /** A key for property that defines the driver classname */
    public static final String DRIVER_CLASS = "dbunit.driverClass";
    /** A key for property that defines the connection url */
    public static final String URL = "dbunit.connectionUrl";
    /** A key for property that defines the username */
    public static final String USERNAME = "dbunit.username";
    /** A key for property that defines the user's password */
    public static final String PASSWORD = "dbunit.password";
    /** A key for property that defines the database schema */
    public static final String SCHEMA = "dbunit.schema";

    /**
     * Creates a new {@link JdbcDatabaseTester} using specific {@link System#getProperty(String)}
     * values as initialization parameters
     */
    public PropertiesBasedJdbcDatabaseTester() throws Exception {
        super(system(DRIVER_CLASS), system(URL), system(USERNAME), system(PASSWORD), system(SCHEMA));
    }

    private static String system(String property) {
        return System.getProperty(property);
    }
}