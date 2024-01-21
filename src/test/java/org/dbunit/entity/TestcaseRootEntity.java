package org.dbunit.entity;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Testcases root entity for JAXB.
 */
@XmlRootElement(name = "testcases")
@Getter
public final class TestcaseRootEntity {

    @XmlAttribute(name = "jdbc-config")
    private String jdbcConfig;

    @XmlAttribute(name = "base-dir")
    private String baseDir;
    
    @XmlElement(name = "testcase")
    private Collection<TestcaseEntity> dbunitTestcase = new LinkedList<>();
}