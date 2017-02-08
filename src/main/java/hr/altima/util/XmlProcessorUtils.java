package hr.altima.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by simon on 8.2.2017.
 */
public class XmlProcessorUtils {

    public static Document getDocumentFromFile(final File inputContent) throws IOException, SAXException, ParserConfigurationException {
        return getXmlDocumentFromInput(new FileInputStream(inputContent));
    }

    public static Document getDocumentFromString(final String inputContent) throws IOException, SAXException, ParserConfigurationException {
        return getXmlDocumentFromInput(new ByteArrayInputStream(inputContent.getBytes()));
    }

    private static Document getXmlDocumentFromInput(final InputStream inputContent) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document parsedInput = builder.parse(inputContent);
        inputContent.close();
        return parsedInput;
    }

    public static String getStringFromDoc(final Document doc) throws TransformerException {

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, result);
        writer.flush();
        return writer.toString();
    }


}
