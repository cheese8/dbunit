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

import lombok.AllArgsConstructor;

import java.util.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 20, 2002
 */
@AllArgsConstructor
public class DatabaseProfile {
    public static final String DATABASE_PROFILE = "dbunit.profile";
    private static final String PROFILE_DRIVER_CLASS = "dbunit.profile.driverClass";
    private static final String PROFILE_URL = "dbunit.profile.url";
    private static final String PROFILE_SCHEMA = "dbunit.profile.schema";
    private static final String PROFILE_USER = "dbunit.profile.user";
    private static final String PROFILE_PASSWORD = "dbunit.profile.password";
    private static final String PROFILE_UNSUPPORTED_FEATURES = "dbunit.profile.unsupportedFeatures";
    private static final String PROFILE_DDL = "dbunit.profile.ddl";
    private static final String PROFILE_MULTILINE_SUPPORT = "dbunit.profile.multiLineSupport";

    private final Properties properties;

    public String getActiveProfile() {
        return properties.getProperty(DATABASE_PROFILE);
    }

    public String getDriverClass() {
        return properties.getProperty(PROFILE_DRIVER_CLASS);
    }

    public String getConnectionUrl() {
        return properties.getProperty(PROFILE_URL);
    }

    public String getSchema() {
        return properties.getProperty(PROFILE_SCHEMA, null);
    }

    public String getUser() {
        return properties.getProperty(PROFILE_USER);
    }

    public String getPassword() {
        return properties.getProperty(PROFILE_PASSWORD);
    }

    public String getProfileDdl() {
        return properties.getProperty(PROFILE_DDL);
    }

    public boolean getProfileMultilineSupport() {
        return Boolean.parseBoolean(properties.getProperty(PROFILE_MULTILINE_SUPPORT));
    }

    public String[] getUnsupportedFeatures() {
        String property = properties.getProperty(PROFILE_UNSUPPORTED_FEATURES);
        if (property == null) {
        	return new String[0];
        }
        List<String> stringList = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(property, ",");
        while (tokenizer.hasMoreTokens()) {
            stringList.add(tokenizer.nextToken().trim());
        }
        return stringList.toArray(new String[0]);
    }
}