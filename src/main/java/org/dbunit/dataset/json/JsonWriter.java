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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dbunit.dataset.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class JsonWriter
{

    private Gson gson;
    private Yaml _yaml;
    private Writer _out;
    private boolean _useFlowStyle;

    public JsonWriter(Writer out)
    {
        this(out, false);
    }

    public JsonWriter(Writer out, boolean useFlowStyle)
    {
        this._out = out;
        this._useFlowStyle = useFlowStyle;
    }

    public void setUseFlowStyle(boolean useFlowStyle)
    {
        this._useFlowStyle = useFlowStyle;
    }

    public void write(IDataSet dataSet) throws DataSetException
    {
        LinkedHashMap<String, List<LinkedHashMap<String, Object>>> dataSetAsMap = dataSetAsMap(dataSet);
        DumperOptions options = new DumperOptions();
        if (_useFlowStyle)
        {
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
            options.setPrettyFlow(true);
        } else
        {
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setIndent(4);
            options.setIndicatorIndent(2);
        }
        gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(dataSetAsMap, _out);
        try {
            _out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedHashMap<String, List<LinkedHashMap<String, Object>>> dataSetAsMap(IDataSet dataSet) throws DataSetException
    {
        LinkedHashMap<String, List<LinkedHashMap<String, Object>>> result = new LinkedHashMap<>();
        ITableIterator iterator = dataSet.iterator();
        while (iterator.next())
        {
            ITableMetaData tableMetaData = iterator.getTableMetaData();
            List<LinkedHashMap<String, Object>> tableRows = new ArrayList<>();
            ITable table = iterator.getTable();
            for (int row = 0; row < table.getRowCount(); row++)
            {
                LinkedHashMap<String, Object> rowMap = new LinkedHashMap<>();
                for (Column column : tableMetaData.getColumns())
                {
                    String columnName = column.getColumnName();
                    Object value = table.getValue(row, columnName);
                    if (value != null)
                    {
                        rowMap.put(columnName, value);
                    }
                }
                tableRows.add(rowMap);
            }
            result.put(tableMetaData.getTableName(), tableRows);
        }
        return result;
    }
}