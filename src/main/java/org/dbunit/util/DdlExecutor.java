/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2017, DbUnit.org
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
package org.dbunit.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.StringTokenizer;

/**
 * Test Helper class for Executing DDL.
 *
 * @author Andrew Landsverk
 * @version $Revision$
 * @since DbUnit 2.6.0
 */

@Slf4j
public final class DdlExecutor {
    private DdlExecutor() {
        // no instances
    }

    /**
     * Execute DDL from the file (by name) against the given {@link Connection},
     * dispatches to executeDdlFile and passes false for ignoreErrors.
     *
     * @param ddlFileName      The name of the DDL file to execute.
     * @param connection       The {@link Connection} to execute the DDL against.
     * @param multiLineSupport If this DataSource supports passing in all the lines at once
     *                         or if it needs to separate on ';'.
     * @throws Exception
     */
    public static void execute(final String ddlFileName, final Connection connection, final boolean multiLineSupport) throws Exception {
        execute(ddlFileName, connection, multiLineSupport, false);
    }

    /**
     * Execute DDL from the file (by name) against the given {@link Connection},
     * dispatches to executeDdlFile.
     *
     * @param ddlFileName      The name of the DDL file to execute.
     * @param connection       The {@link Connection} to execute the DDL against.
     * @param multiLineSupport If this DataSource supports passing in all the lines at once
     *                         or if it needs to separate on ';'.
     * @param ignoreErrors     Set this to true if you want syntax errors to be ignored.
     * @throws Exception
     */
    public static void execute(final String ddlFileName, final Connection connection, final boolean multiLineSupport, final boolean ignoreErrors) throws Exception {
        //final File ddlFile = TestUtils.getFile(ddlFileName);
        String fileName = "src/test/resources/" + ddlFileName;
        final File ddlFile = new File(fileName);
        executeDdlFile(ddlFile, connection, multiLineSupport, ignoreErrors);
    }

    /**
     * Executes DDL from the {@link File} against the given {@link Connection}.
     * Retrieves the multiLineSupport parameter from the profile.
     *
     * @param ddlFile    The {@link File} object of the DDL file to execute.
     * @param connection The {@link Connection} to execute the DDL against.
     * @throws Exception
     */
    /*
    public static void executeDdlFile(final File ddlFile, final Connection connection, boolean multiLineSupport) throws Exception {
        //final boolean multiLineSupport = DatabaseEnvironment.getInstance().getProfile().getProfileMultilineSupport();
        log.debug("Executing DDL from file={}, multiLineSupport={}", ddlFile, multiLineSupport);
        executeDdlFile(ddlFile, connection, multiLineSupport);
    }
     */

    /**
     * Executes DDL from the {@link File} against the given {@link Connection}.
     * Retrieves the multiLineSupport parameter from the profile and passes
     * false for ignoreErrors.
     *
     * @param ddlFile          The {@link File} object of the DDL file to execute.
     * @param connection       The {@link Connection} to execute the DDL against.
     * @param multiLineSupport If this DataSource supports passing in all the lines at once
     *                         or if it needs to separate on ';'.
     * @throws Exception
     */
    public static void executeDdlFile(final File ddlFile, final Connection connection, final boolean multiLineSupport) throws Exception {
        executeDdlFile(ddlFile, connection, multiLineSupport, false);
    }

    /**
     * Execute DDL from the {@link File} against the given {@link Connection}.
     *
     * @param ddlFile          The {@link File} object of the DDL file to execute.
     * @param connection       The {@link Connection} to execute the DDL against.
     * @param multiLineSupport If this DataSource supports passing in all the lines at once
     *                         or if it needs to separate on ';'.
     * @param ignoreErrors     Set this to true if you want syntax errors to be ignored.
     * @throws Exception
     */
    public static void executeDdlFile(final File ddlFile, final Connection connection, final boolean multiLineSupport, final boolean ignoreErrors) throws Exception {
        final String sql = readSqlFromFile(ddlFile);
        if (multiLineSupport) {
            executeSql(connection, sql, ignoreErrors);
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(sql, ";");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();
            if (token.length() > 0) {
                executeSql(connection, token, ignoreErrors);
            }
        }
    }

    /**
     * Execute an un-prepared SQL statement against the given
     * {@link Connection}, passes false to ignoreErrors.
     *
     * @param connection The {@link Connection} to execute against
     * @param sql        The SQL {@link String} to execute
     * @throws SQLException
     */
    public static void executeSql(final Connection connection, final String sql) throws SQLException {
        executeSql(connection, sql, false);
    }

    /**
     * Execute an un-prepared SQL statement against the given
     * {@link Connection}.
     *
     * @param connection   The {@link Connection} to execute against
     * @param sql          The SQL {@link String} to execute
     * @param ignoreErrors Set this to true if you want syntax errors to be ignored.
     * @throws SQLException
     */
    public static void executeSql(final Connection connection, final String sql, final boolean ignoreErrors) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLSyntaxErrorException exception) {
            if (!ignoreErrors) {
                throw exception;
            }
            log.debug("Ignoring error executing DDL={}", exception.getMessage());
        }
    }

    private static String readSqlFromFile(final File ddlFile) throws IOException {
        final BufferedReader sqlReader = new BufferedReader(new FileReader(ddlFile));
        final StringBuilder sqlBuffer = new StringBuilder();
        while (sqlReader.ready()) {
            String line = sqlReader.readLine();
            if (!line.startsWith("-")) {
                sqlBuffer.append(line);
            }
        }
        sqlReader.close();
        return sqlBuffer.toString();
    }
}