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

package org.dbunit.ant;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.FilterSet;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;

/**
 * This element is a container for Queries. It facilitates reuse
 * through references. Using Ant 1.6 and greater, references can be
 * defined in a single build file and <i>import</i>ed into many others.
 * An example of where this is useful follows:
 * <p>
 * In our database
 * we have INDIVIDUALS which must have an associated NAME_INFO and
 * at least one IND_ADDRESS. The developer creating a dataset for
 * his/her tests probably won't know all the details of what relationships are
 * expected, and if he did, its an error prone and repetitive task
 * to create the correct SQL for entities in each dataset.
 * Missing a related table, not only creates invalid data for your tests,
 * but also is likely to cause DBUnit setUp() failures from foreign key
 * constraint violation errors.
 * (example: If a previous test had inserted INDIVIDUALS
 * and NAME_INFO and my test tries to delete only the INDIVIDUALS, the
 * NAME_INFO.IND_ID constraint would be violated)
 * <p>
 * <p>
 * Each queryset is internally converted to a <code>QueryDataSet</code> and then
 * combined using a <code>CompositeDataSet</code>. This means that you can use
 * more than one <code>query</code> element for any given table provided they
 * are nested within separate <code>queryset</code>s.
 * <p>
 * Usage:
 *
 * <pre>
 * &lt;!-- ======== Define the reusable reference ========== --&gt;
 *
 * &lt;queryset id="individuals"&gt;
 *    &lt;query name="INDIVIDUALS" sql="
 *      SELECT * FROM INDIVIDUALS WHERE IND_ID IN (@subQuery@)"/&gt;
 *
 *    &lt;query name="NAME_INFO" sql="
 *      SELECT B.* FROM INDIVIDUALS A, NAME_INFO B
 *      WHERE A.IND_ID IN (@subQuery@)
 *      AND B.IND_ID = A.IND_ID"/&gt;
 *
 *    &lt;query name="IND_ADDRESSES" sql="
 *      SELECT B.* FROM INDIVIDUALS A, IND_ADDRESSES B
 *      WHERE A.IND_ID IN (@subQuery@)
 *      AND B.IND_ID = A.IND_ID"/&gt;
 * &lt;/queryset&gt;
 *
 * &lt;!-- ========= Use the reference ====================== --&gt;
 *
 * &lt;dbunit driver="${jdbcDriver}"
 *     url="${jdbcURL}" userid="${jdbcUser}" password="${jdbcPassword}"&gt;
 *   &lt;export dest="${dest}"&gt;
 *   &lt;queryset refid="individuals"&gt;
 *      &lt;filterset&gt;
 *        &lt;filter token="subQuery" value="
 *          SELECT IND_ID FROM INDIVIDUALS WHERE USER_NAME = 'UNKNOWN'"/&gt;
 *      &lt;/filterset&gt;
 *   &lt;/queryset&gt;
 *
 *   &lt;/export&gt;
 * &lt;/dbunit&gt;
 *
 * </pre>
 *
 * @author Lenny Marks lenny@aps.org
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.0 (Sep. 13 2004)
 */
@Slf4j
public class QuerySet extends ProjectComponent {
    @Getter
    private String id;
    @Getter
    private String refid;
    private final List<Query> queries = new ArrayList<>();
    private final List<FilterSet> filterSets = new ArrayList<>();

    private final static String ERR_MSG = "Cannot specify 'id' and 'refid' attributes together in queryset.";

    public QuerySet() {
        super();
    }

    public void addQuery(Query query) {
        log.debug("addQuery(query={}) - start", query);
        queries.add(query);
    }

    public void addFilterSet(FilterSet filterSet) {
        log.debug("addFilterSet(filterSet={}) - start", filterSet);
        filterSets.add(filterSet);
    }

    public void setId(String string) {
        log.debug("setId(string={}) - start", string);
        if (refid != null) {
            throw new BuildException(ERR_MSG);
        }
        id = string;
    }

    public void setRefid(String string) {
        log.debug("setRefid(string={}) - start", string);
        if (id != null) {
            throw new BuildException(ERR_MSG);
        }
        refid = string;
    }

    public List<Query> getQueries() {
        log.debug("getQueries() - start");
        for (Query query : queries) {
            replaceTokens(query);
        }
        return queries;
    }

    private void replaceTokens(Query query) {
        log.debug("replaceTokens(query={}) - start", query);
        for (FilterSet filterSet : filterSets) {
            query.setSql(filterSet.replaceTokens(query.getSql()));
        }
    }

    public void copyQueriesFrom(QuerySet referenced) {
        log.debug("copyQueriesFrom(referenced={}) - start", referenced);
        for (Query query : referenced.queries) {
            addQuery(query);
        }
    }

    public QueryDataSet getQueryDataSet(IDatabaseConnection connection) throws AmbiguousTableNameException {
        log.debug("getQueryDataSet(connection={}) - start", connection);

        //incorporate queries from referenced query-set
        String refid = getRefid();
        if (refid != null) {
            QuerySet referenced = (QuerySet) getProject().getReference(refid);
            copyQueriesFrom(referenced);
        }

        QueryDataSet partialDataSet = new QueryDataSet(connection);
        for (Query query : getQueries()) {
            partialDataSet.addTable(query.getName(), query.getSql());
        }
        return partialDataSet;
    }
}