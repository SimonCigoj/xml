package hr.altima.services;

import com.google.common.base.Preconditions;
import hr.altima.util.FileUtils;
import hr.altima.util.JsonUtils;
import hr.altima.util.XmlProcessorUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by simon on 3.2.2017.
 */
@Service
public class XmlProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(XmlProcessorService.class);

    @Value("${file.processing.batch.size}")
    private int batchSize;

    @Value("${file.processing.report.json}")
    private String jsonReportFileName;

    public List<File> getFilesToProcess(final String inputFolder){
        Preconditions.checkArgument(StringUtils.isNotEmpty(inputFolder));

        File folder = new File(inputFolder);

        if(!folder.exists()){
            logger.error("Input folder ("+inputFolder+") was not found");
            return new ArrayList<>();
        }

        File[] listOfFiles = folder.listFiles();

        return Arrays.stream(listOfFiles)
                .limit(batchSize)
                .filter(file -> file.getName().endsWith(".xml") || file.getName().endsWith(".XML"))
                .collect(Collectors.toList());
    }



    public Document processXmlDocument(Document inputXmlDocument, final String xpathQuery) throws XPathExpressionException, TransformerException {
       Preconditions.checkArgument(inputXmlDocument != null);

       if (StringUtils.isNotBlank(xpathQuery)) {

            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList nodeList = (NodeList) xPath.compile(xpathQuery).evaluate(inputXmlDocument, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                boolean nodeHaseValue = node != null && node.getFirstChild() != null && StringUtils.isNotBlank(node.getFirstChild().getNodeValue());

                if (nodeHaseValue) {
                    node.getFirstChild().setNodeValue(node.getFirstChild().getNodeValue().toUpperCase());
                }
            }
        }
        return inputXmlDocument;
    }

    public void createOutputFile(final Document outputXml, final String outputFileName) throws IOException, TransformerException {
        Preconditions.checkArgument(outputXml != null);
        Preconditions.checkArgument(StringUtils.isNotEmpty(outputFileName));

        String outputContent = XmlProcessorUtils.getStringFromDoc(outputXml);
        FileUtils.createFileWithContent(outputContent,outputFileName);
    }

    public synchronized void createOrUpdateReport(final String outFilename, final String rootElement) throws IOException, ParseException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(rootElement));
        Preconditions.checkArgument(StringUtils.isNotEmpty(outFilename));

        File file = new File(jsonReportFileName + "/report.json");
        JSONObject reportJson;
        JSONObject newLog = newLogEntry(outFilename, rootElement);

        if (file.exists()) {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            reportJson = (JSONObject) obj;
            JSONArray logs = (JSONArray) reportJson.get("logs");
            logs.add(newLog);
        } else {
            reportJson = new JSONObject();
            JSONArray logs = new JSONArray();
            logs.add(newLog);
            reportJson.put("logs", logs);
        }
        String jsonReportString = JsonUtils.preetifyJson(reportJson.toJSONString());
        FileUtils.createFileWithContent(jsonReportString,file.getPath(),false);
    }

    public void deleteInput(final String inputFilename){
        Preconditions.checkArgument(StringUtils.isNotEmpty(inputFilename));

        File file = new File(inputFilename);

        if(!file.delete()){
            logger.warn("Delete operation for input file "+inputFilename+" is failed.");
        }
    }

    private JSONObject newLogEntry(final String outFilename, final String rootElement){
        Preconditions.checkArgument(StringUtils.isNotEmpty(rootElement));
        Preconditions.checkArgument(StringUtils.isNotEmpty(outFilename));

        JSONObject newLog = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        newLog.put("outFilename", outFilename);
        newLog.put("rootEl", rootElement);
        newLog.put("time", LocalDateTime.now().format(formatter));
        return newLog;
    }

}
