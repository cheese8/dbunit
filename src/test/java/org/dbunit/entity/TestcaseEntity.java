package org.dbunit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Testcase entity for JAXB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public final class TestcaseEntity {

    @XmlAttribute(name = "jdbc-config")
    private String jdbcConfig;

    @XmlAttribute(name = "base-dir")
    private String baseDir;

    @XmlAttribute(name = "clazz")
    private String clazz;

    @XmlElement(name = "testcase")
    private Collection<Testcase> testcases = new LinkedList<>();
}
