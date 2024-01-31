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
package org.dbunit.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dbunit.database.AmbiguousTableNameException;

/**
 * Associates a table name with an arbitrary object. Moreover the
 * order of the added table names is maintained and the ordered table
 * names can be retrieved via {@link #getTableNames()}.
 * <p>
 * The map ensures that one table name can only be added once.
 * </p>
 * <p>
 * TODO In the future it might be discussed if a ListOrderedMap (apache-commons-collections) can/should be used.
 *
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$
 * @since 2.4.0
 */
@Slf4j
public class OrderedTableNameMap {
    /**
     * The map for fast access to the existing table names and for
     * associating an arbitrary object with a table name
     */
    private final Map<String, Object> tableMap = new HashMap<>();
    /**
     * Chronologically ordered list of table names - keeps the order
     * in which the table names have been added as well as the case in
     * which the table has been added
     */
    private final List<String> tableNames = new ArrayList<>();

    private String lastTableNameOverride;

    /**
     * Whether case-sensitive table names should be used. Defaults to false.
     */
    private final boolean caseSensitiveTableNames;

    /**
     * Creates a new map which does strictly force that one table can only occur once.
     *
     * @param caseSensitiveTableNames Whether table names should be case-sensitive
     */
    public OrderedTableNameMap(boolean caseSensitiveTableNames) {
        this.caseSensitiveTableNames = caseSensitiveTableNames;
    }

    /**
     * Returns the object associated with the given table name
     *
     * @param tableName The table name for which the associated object is retrieved
     * @return The object that has been associated with the given table name
     */
    public Object get(String tableName) {
        String correctedCaseTableName = getTableName(tableName);
        return tableMap.get(correctedCaseTableName);
    }

    /**
     * Provides the ordered table names having the same order in which the table
     * names have been added via {@link #add(String, Object)}.
     *
     * @return The list of table names ordered in the sequence as
     * they have been added to this map
     */
    public String[] getTableNames() {
        return tableNames.toArray(new String[0]);
    }

    /**
     * Checks if this map contains the given table name
     *
     * @return Returns <code>true</code> if the map of tables contains the given table name
     */
    public boolean containsTable(String tableName) {
        String correctedCaseTableName = getTableName(tableName);
        return tableMap.containsKey(correctedCaseTableName);
    }

    /**
     * @param tableName The table name to check
     * @return <code>true</code> if the given tableName matches the last table that has been added to this map.
     */
    public boolean isLastTable(String tableName) {
        log.debug("isLastTable(tableName={}) - start", tableName);
        if (CollectionUtils.isEmpty(tableNames)) {
            return false;
        }
        String lastTable = getLastTableName();
        String lastTableCorrectCase = getTableName(lastTable);
        String inputTableCorrectCase = getTableName(tableName);
        return lastTableCorrectCase.equals(inputTableCorrectCase);
    }

    /**
     * @return The name of the last table that has been added to this map. Returns <code>null</code> if no
     * table has been added yet.
     */
    public String getLastTableName() {
        log.debug("getLastTableName() - start");
        if (lastTableNameOverride != null) {
            return lastTableNameOverride;
        }
        if (!CollectionUtils.isEmpty(tableNames)) {
            return tableNames.get(tableNames.size() - 1);
        }
        return null;
    }


    public void setLastTable(String tableName) throws NoSuchTableException {
        log.debug("setLastTable(name{}) - start", tableName);
        if (!containsTable(tableName)) {
            throw new NoSuchTableException(tableName);
        }
        lastTableNameOverride = tableName;
    }

    /**
     * Adds the given table name to the map of table names, associating
     * it with the given object.
     *
     * @param tableName The table name to be added
     * @param object    Object to be associated with the given table name. Can be null
     * @throws AmbiguousTableNameException If the given table name already exists
     */
    public void add(String tableName, Object object) throws AmbiguousTableNameException {
        log.debug("add(tableName={}, object={}) - start", tableName, object);
        // Get the table name in the correct case
        String tableNameCorrectedCase = getTableName(tableName);
        // prevent table name conflict
        if (containsTable(tableNameCorrectedCase)) {
            throw new AmbiguousTableNameException(tableNameCorrectedCase);
        }
        tableMap.put(tableNameCorrectedCase, object);
        tableNames.add(tableName);
        // Reset the override of the lastTableName
        lastTableNameOverride = null;
    }

    /**
     * @return The values of this map ordered in the sequence they have been added
     */
    public Collection<Object> orderedValues() {
        log.debug("orderedValues() - start");
        List<Object> orderedValues = new ArrayList<>(tableNames.size());
        for (String tableName : tableNames) {
            Object object = get(tableName);
            orderedValues.add(object);
        }
        return orderedValues;
    }

    /**
     * Updates the value associated with the given table name. Must be invoked if
     * the table name has already been added before.
     *
     * @param tableName The table name for which the association should be updated
     * @param object    The new object to be associated with the given table name
     */
    public void update(String tableName, Object object) {
        log.debug("update(tableName={}, object={}) - start", tableName, object);
        // prevent table name conflict
        if (!containsTable(tableName)) {
            throw new IllegalArgumentException("The table name '" + tableName + "' does not exist in the map");
        }
        tableName = getTableName(tableName);
        tableMap.put(tableName, object);
    }

    /**
     * Returns the table name in the correct case (for example as upper case string)
     *
     * @param tableName The input table name to be resolved
     * @return The table name for the given string in the correct case.
     */
    public String getTableName(String tableName) {
        log.debug("getTableName(tableName={}) - start", tableName);
        String result = tableName;
        if (!caseSensitiveTableNames) {
            // "Locale.ENGLISH" Fixes bug #1537894 when clients have a special locale like turkish. (for release 2.4.3)
            result = tableName.toUpperCase(Locale.ENGLISH);
        }
        log.debug("getTableName(tableName={}) - end - result={}", tableName, result);
        return result;
    }

    public String toString() {
        return getClass().getName() + "[" + "tableNames=" + tableNames + ", tableMap=" + tableMap + ", caseSensitiveTableNames=" + caseSensitiveTableNames + "]";
    }
}