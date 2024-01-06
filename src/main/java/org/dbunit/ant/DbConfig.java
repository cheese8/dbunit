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
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Property;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;

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
    private Set properties = new HashSet();
    private Set features = new HashSet();

    public void addProperty(Property property) {
        log.trace("addProperty(property={}) - start)", property);
        this.properties.add(property);
    }

    public void addFeature(Feature feature) {
        log.trace("addFeature(feature={}) - start)", feature);
        this.features.add(feature);
    }

    /**
     * Copies the parameters set in this configuration via ant into the given
     * {@link DatabaseConfig} that is used by the dbunit connection.
     * @param config The configuration object to be initialized/updated
     * @throws DatabaseUnitException 
     */
    public void copyTo(DatabaseConfig config) throws DatabaseUnitException 
    {
        Properties javaProps = new Properties();
        
        for (Iterator iterator = this.features.iterator(); iterator.hasNext();) {
            Feature feature = (Feature)iterator.next();
            
            String propName = feature.getName();
            String propValue = String.valueOf(feature.isValue());

            log.debug("Setting property {}", feature);
            javaProps.setProperty(propName, propValue);
        }
        
        // Copy the properties into java.util.Properties
        for (Iterator iterator = this.properties.iterator(); iterator.hasNext();) {
            Property prop = (Property) iterator.next();
            
            String propName = prop.getName();
            String propValue = prop.getValue();

            if(propName==null)
                throw new NullPointerException("The propName must not be null");
            
            if(propValue==null)
                throw new NullPointerException("The propValue must not be null");

            log.debug("Setting property {}", prop);
            javaProps.setProperty(propName, propValue);
        }
        
        config.setPropertiesByString(javaProps);
    }
}