package hr.altima.services;

import hr.altima.util.XmlProcessorUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

/**
 * Created by simon on 8.2.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlProcessorServiceTest {

    String testInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns:department xmlns:ns=\"urn:example\">\n" +
            "\t<employee>\n" +
            "\t\t<id1>1</id1>\n" +
            "\t\t<ns:id2>test1</ns:id2>\n" +
            "\t\t<team-member>\n" +
            "\t\t\t<fk1>2</fk1>\n" +
            "\t\t\t<ns:fk2>test2</ns:fk2>\n" +
            "\t\t</team-member>\n" +
            "\t\t<team-member>\n" +
            "\t\t\t<fk1>3</fk1>\n" +
            "\t\t\t<ns:fk2>C</ns:fk2>\n" +
            "\t\t</team-member>\n" +
            "\t</employee>\n" +
            "\t<employee>\n" +
            "\t\t<id1>2</id1>\n" +
            "\t\t<ns:id2>B</ns:id2>\n" +
            "\t\t<manager>\n" +
            "\t\t\t<fk1>1</fk1>\n" +
            "\t\t\t<ns:fk2>A</ns:fk2>\n" +
            "\t\t</manager>\n" +
            "\t</employee>\n" +
            "\t<employee>\n" +
            "\t\t<id1>3</id1>\n" +
            "\t\t<ns:id2>C</ns:id2>\n" +
            "\t\t<manager>\n" +
            "\t\t\t<fk1>1</fk1>\n" +
            "\t\t\t<ns:fk2>test3</ns:fk2>\n" +
            "\t\t</manager>\n" +
            "\t</employee>\n" +
            "</ns:department>";

    @InjectMocks
    XmlProcessorService processorService;

    @Test
    public void testProcessXmlDocumentForUpercase() throws Exception {

        String expression = "/*[local-name()='department']/*[local-name()='employee']/*[local-name()='id2'] " +
                " | /*[local-name()='department']/*[local-name()='employee']/*[local-name()='team-member']/*[local-name()='fk2']" +
                " | /*[local-name()='department']/*[local-name()='employee']/*[local-name()='manager']/*[local-name()='fk2']";

        Document inputXml = XmlProcessorUtils.getDocumentFromString(testInput);
        Document processedXml = processorService.processXmlDocument(inputXml, expression);
        String processedXmlString = XmlProcessorUtils.getStringFromDoc(processedXml);

        Assert.assertTrue(processedXmlString.contains("<ns:id2>TEST1</ns:id2>"));
        Assert.assertTrue(processedXmlString.contains("<ns:fk2>TEST2</ns:fk2>"));
        Assert.assertTrue(processedXmlString.contains("<ns:fk2>TEST3</ns:fk2>"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testProcessXmlDocumentIllegalArgument() throws Exception {
        processorService.processXmlDocument(null, "");
    }

    @Test
    public void testProcessXmlDocumentNoProcessingIfEmptyypathQuery() throws Exception {

        Document inputXml = XmlProcessorUtils.getDocumentFromString(testInput);
        Document processedXml = processorService.processXmlDocument(inputXml, "");
        String processedXmlString = XmlProcessorUtils.getStringFromDoc(processedXml);

        Assert.assertTrue(processedXmlString.contains("<ns:id2>test1</ns:id2>"));
        Assert.assertTrue(processedXmlString.contains("<ns:fk2>test2</ns:fk2>"));
        Assert.assertTrue(processedXmlString.contains("<ns:fk2>test3</ns:fk2>"));
    }


}