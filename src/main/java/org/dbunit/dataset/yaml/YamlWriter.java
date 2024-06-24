package org.dbunit.dataset.yaml;

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
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.Writer;
import java.util.*;

/**
 * @author Björn Beskow
 * @version $Revision$ $Date$
 */
class YamlWriter
{

    private Yaml _yaml;
    private Writer _out;
    private boolean _useFlowStyle;
    private Map<String, String> replacementsMap;

    public YamlWriter(Writer out, String[] replacements)
    {
        this(out, false, replacements);
    }

    public YamlWriter(Writer out, boolean useFlowStyle, String[] replacements)
    {
        this._out = out;
        this._useFlowStyle = useFlowStyle;
        Map<String, String> replacementsMap = new HashMap<>();
        if (replacements.length > 0) {
            for(int i=0;i<replacements.length;i=i+2) {
                replacementsMap.put(replacements[i], replacements[i+1]);
            }
        }
        this.replacementsMap = replacementsMap;
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
        _yaml = new Yaml(options);
        _yaml.dump(dataSetAsMap, _out);
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
                Column[] columns = tableMetaData.getColumns();
                Arrays.sort(columns);
                for (Column column : columns)
                {
                    String columnName = column.getColumnName();
                    String entryValue = replacementsMap.get(columnName);
                    Object value = table.getValue(row, columnName);
                    if (value != null)
                    {
                        rowMap.put(columnName, entryValue!= null ? entryValue : value);
                    }
                }
                tableRows.add(rowMap);
            }
            result.put(tableMetaData.getTableName(), tableRows);
        }
        return result;
    }
}