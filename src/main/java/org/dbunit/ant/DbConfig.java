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
package org.dbunit.ant;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Property;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.util.Assert;

/**
 * The database configuration for the ant task.
 *
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
@Slf4j
@NoArgsConstructor
public class DbConfig extends ProjectComponent {
    private final Set<Property> properties = new HashSet<>();
    private final Set<Feature> features = new HashSet<>();

    public void addProperty(Property property) {
        properties.add(property);
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }

    /**
     * Copies the parameters set in this configuration via ant into the given
     * {@link DatabaseConfig} that is used by the dbunit connection.
     *
     * @param config The configuration object to be initialized/updated
     */
    public void copyTo(DatabaseConfig config) throws DatabaseUnitException {
        Properties javaProps = new Properties();
        for (Feature feature : this.features) {
            String propName = feature.getName();
            String propValue = String.valueOf(feature.isValue());
            log.debug("Setting property {}", feature);
            javaProps.setProperty(propName, propValue);
        }

        for (Property prop : this.properties) {
            String propName = prop.getName();
            String propValue = prop.getValue();
            Assert.assertThat(propName != null, new NullPointerException("The propName must not be null"));
            Assert.assertThat(propValue != null, new NullPointerException("The propValue must not be null"));
            log.debug("Setting property {}", prop);
            javaProps.setProperty(propName, propValue);
        }
        config.setPropertiesByString(javaProps);
    }
}