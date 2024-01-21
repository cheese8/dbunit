package org.dbunit.entity;

import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;

/**
 * Testcase root entity loader for JAXB.
 */
public final class TestcaseRootEntityLoader {
    
    /**
     * Load testcase root entity from XML.
     *
     * @param file testcase root entity file
     * @return testcase entity for JAXB
     */
    @SneakyThrows(JAXBException.class)
    public TestcaseRootEntity load(final String file) {
        InputStream inputStream = TestcaseRootEntityLoader.class.getClassLoader().getResourceAsStream(file);
        if (null == inputStream) {
            return new TestcaseRootEntity();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(TestcaseRootEntity.class);
        return (TestcaseRootEntity) jaxbContext.createUnmarshaller().unmarshal(inputStream);
    }
}