/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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
package org.dbunit.database.search;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.util.search.Edge;

/**
 * Implementation of an edge representing a foreign key (FK)  relationship between two
 * tables.<br>
 * The <code>from</code> node is the table which have the FK, while the
 * <code>to</code> node is the table with the PK. In other words, the edge
 * A->B means FK(A) = PK(B).<br>
 *
 * <strong>NOTE:</strong> only single-column PKs are supported at this moment
 *
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2 (Sep 9, 2005)
 */
@Slf4j
@Getter
public class ForeignKeyRelationshipEdge extends Edge {
    private final String fkColumn;
    private final String pkColumn;

    /**
     * Creates an edge representing a FK.
     *
     * @param tableFrom table that has the FK
     * @param tableTo   table that has the PK
     * @param fkColumn  name of the FK column on tableFrom
     * @param pkColumn  name of the PK column on tableTo
     */

    public ForeignKeyRelationshipEdge(String tableFrom, String tableTo, String fkColumn, String pkColumn) {
        super(tableFrom, tableTo);
        this.fkColumn = fkColumn;
        this.pkColumn = pkColumn;
    }

    public String toString() {
        return getFrom() + "(" + fkColumn + ")->" + getTo() + "(" + pkColumn + ")";
    }

    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fkColumn == null) ? 0 : fkColumn.hashCode());
        result = prime * result + ((pkColumn == null) ? 0 : pkColumn.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ForeignKeyRelationshipEdge other = (ForeignKeyRelationshipEdge) obj;
        if (fkColumn == null) {
            if (other.fkColumn != null) {
                return false;
            }
        } else if (!fkColumn.equals(other.fkColumn)) {
            return false;
        }
        if (pkColumn == null) {
            return other.pkColumn == null;
        } else {
            return pkColumn.equals(other.pkColumn);
        }
    }

    public int compareTo(Object o) {
        log.debug("compareTo(o={}) - start", o);
        ForeignKeyRelationshipEdge that = (ForeignKeyRelationshipEdge) o;
        int result = super.compareTo(that);
        if (result == 0) {
            result = this.pkColumn.compareTo(that.pkColumn);
        }
        if (result == 0) {
            result = this.fkColumn.compareTo(that.fkColumn);
        }
        return result;
    }
}