package org.dbunit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Testcase entity for JAXB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public final class Testcase {

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "clazz")
    private String clazz;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "src")
    private String src;

    @XmlElement(name = "format")
    private String format;

    @XmlElement(name = "nullToken")
    private String nullToken;

    @XmlElement(name = "ordered")
    private String ordered;
}