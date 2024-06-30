package org.dbunit.dataset.json;

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

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import java.io.*;

/**
 * Reads and writes flat YAML-based dataset documents. Contrary to the flat XML layout,
 * columns are calculated by parsing the entire data set, not just the first row.
 * <br/><br/>
 * The format looks like this:
 * <br/>
 * <pre>
 * &lt;table_name&gt;:
 *   - &lt;column&gt;: &lt;value&gt;
 *     &lt;column&gt;: &lt;value&gt;
 *             ...
 *   - &lt;column&gt;: &lt;value&gt;
 *     &lt;column&gt;: &lt;value&gt;
 *             ...
 *        ...
 *    ...
 * </pre>
 * <br/>
 * I.e.:
 * <br/>
 * <pre>
 * TEST_TABLE:
 *   - COL0: "row 0 col 0"
 *     COL1: "row 0 col 1"
 *     COL2: "row 0 col 2"
 *   - COL1: "row 1 col 1"
 * SECOND_TABLE:
 *   - COL0: "row 0 col 0"
 *     COL1: "row 0 col 1"
 * EMPTY_TABLE: []
 * </pre>
 *
 * @author Björn Beskow
 * @version $Revision$ $Date$
 */
public class JsonDataSet extends CachedDataSet
{

    /**
     * Creates a YAML dataset based on a yaml file
     */
    public JsonDataSet(File file) throws IOException, DataSetException
    {
        super(new JsonProducer(file), true);
    }

    /**
     * Write the specified dataset to the specified output stream as YAML.
     */
    public static void write(IDataSet dataSet, OutputStream out, boolean sortColumn, String[] replacements)
    throws DataSetException
    {
        write(dataSet, new OutputStreamWriter(out), sortColumn, replacements);
    }

    /**
     * Write the specified dataset to the specified writer as YAML.
     */
    public static void write(IDataSet dataSet, Writer out, boolean sortColumn, String[] replacements)
    throws DataSetException
    {
        JsonWriter writer = new JsonWriter(out, sortColumn, replacements);
        writer.write(dataSet);
    }

}