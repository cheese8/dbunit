package org.dbunit.entity;

import lombok.SneakyThrows;
import org.dbunit.ant.Operation;
import org.dbunit.database.IDatabaseConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Testcases loader for JAXB.
 */
public final class TestcasesLoader {

    /**
     * Load testcases from XML.
     *
     * @param file testcases file
     * @return testcases for JAXB
     */
    @SneakyThrows(JAXBException.class)
    public Testcases load(final String file) {
        InputStream inputStream = TestcasesLoader.class.getClassLoader().getResourceAsStream(file);
        if (null == inputStream) {
            return new Testcases();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(Testcases.class);
        return (Testcases) jaxbContext.createUnmarshaller().unmarshal(inputStream);
    }

    public static void main(String[] args) {
        TestcasesLoader loader = new TestcasesLoader();
        Testcases root = loader.load("operation-test-file.xml");
        //IDatabaseConnection connection = root.getJdbcConfig();
        Collection<Testcase> testcases = root.getTestcases();
        for (Testcase entity : testcases) {
            //Operation operation = (Operation) entity;
            //operation.execute(connection);
        }

    }
}