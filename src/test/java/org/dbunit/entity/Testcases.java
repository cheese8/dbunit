package org.dbunit.entity;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Testcases for JAXB.
 */
@XmlRootElement(name = "testcases")
@Getter
public final class Testcases {

    @XmlAttribute(name = "jdbc-config")
    private String jdbcConfig;

    @XmlAttribute(name = "base-dir")
    private String baseDir;

    @XmlElement(name = "testcase")
    private Collection<Testcase> testcases = new LinkedList<>();
}