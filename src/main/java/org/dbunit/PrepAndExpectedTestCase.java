/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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

import org.dbunit.dataset.IDataSet;

/**
 * Test case supporting prep data and expected data.
 *
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public interface PrepAndExpectedTestCase {
    /**
     * Configure the test. Call this method before performing the test steps.
     *
     * @param verifyTableDefinitions Table definitions to verify after test execution.
     * @param prepDataFiles          The prep data files (as classpath resources) to load and
     *                               insert contents into the database as seed data.
     * @param expectedDataFiles      The expected data files (as classpath resources) to load as
     *                               expected data and verify actual data matches at test end.
     */
    void configureTest(VerifyTableDefinition[] verifyTableDefinitions, String[] prepDataFiles, String[] expectedDataFiles, String datasetId) throws Exception;

    /**
     * Execute pre-test steps. Call this method before performing the test steps.
     */
    void preTest() throws Exception;

    /**
     * Convenience method to call configureTest() and preTest().
     *
     * @param verifyTables      Table definitions to verify after test execution.
     * @param prepDataFiles     The prep data files (as classpath resources) to load and
     *                          insert contents into the database as seed data.
     * @param expectedDataFiles The expected data files (as classpath resources) to load as
     *                          expected data and verify actual data matches at test end.
     */
    void preTest(VerifyTableDefinition[] verifyTables, String[] prepDataFiles, String[] expectedDataFiles, String datasetId) throws Exception;

    /**
     * Run the DbUnit test.
     *
     * @param verifyTables      Table definitions to verify after test execution.
     * @param prepDataFiles     The prep data files (as classpath resources) to load and
     *                          insert contents into the database as seed data.
     * @param expectedDataFiles The expected data files (as classpath resources) to load as
     *                          expected data and verify actual data matches at test end.
     * @param testSteps         The test steps to run.
     * @return User defined object from running the test steps.
     * @since 2.5.2
     */
    Object runTest(VerifyTableDefinition[] verifyTables, String[] prepDataFiles, String[] expectedDataFiles, PrepAndExpectedTestCaseSteps testSteps) throws Exception;

    /**
     * Execute all post-test steps. Call this method after performing the test
     * steps.
     */
    void postTest() throws Exception;

    /**
     * Execute post-test steps. Call this method after performing the test
     * steps.
     *
     * @param verifyData Specify true to perform verify data steps, false to not.
     *                   Useful to specify false when test has failure in progress
     *                   (e.g. an exception) and verifying data would fail, masking
     *                   original test failure.
     */
    void postTest(boolean verifyData) throws Exception;

    /**
     * For the provided VerifyTableDefinitions, verify each table's actual
     * results are as expected.
     */
    void verifyData() throws Exception;

    /**
     * Cleanup tables specified in prep and expected datasets, using the
     * provided databaseTester. See
     * {@link org.dbunit.IDatabaseTester#onTearDown()}.
     */
    void cleanupData() throws Exception;

    /**
     * Get the prep dataset, created from the prepDataFiles.
     *
     * @return The prep dataset.
     */
    IDataSet getPrepDataset();

    /**
     * Get the expected dataset, created from the expectedDataFiles.
     *
     * @return The expected dataset.
     */
    IDataSet getExpectedDataset();
}