package hr.altima.controller;

import hr.altima.services.XmlProcessorService;
import hr.altima.util.XmlProcessorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

/**
 * Created by simon on 3.2.2017.
 */
@RestController
@RequestMapping(value="/rest")
public class XmlProcessorController {

    private static final Logger logger = LoggerFactory.getLogger(XmlProcessorController.class);

    @Value("${file.processing.output.folder}")
    private String outputFolder;

    @Value("${file.processing.xpath.query}")
    private String xpathQuery;

    @Autowired
    XmlProcessorService xmlProcessorService;

    @RequestMapping(value="/xml-processor",method = RequestMethod.POST, consumes="application/xml" )
    public String postTest(@RequestBody String xml){

        try {

            String outFilename = "fromPost_" + System.currentTimeMillis() + ".xml";
            Document input = XmlProcessorUtils.getDocumentFromString(xml);
            Document output = xmlProcessorService.processXmlDocument(input, xpathQuery);
            xmlProcessorService.createOutputFile(output, outputFolder + "/" + outFilename);
            xmlProcessorService.createOrUpdateReport(outFilename, input.getDocumentElement().getNodeName());
            logger.info("file from POST successfuly procesed to " + outFilename);
            return "SUCCESS";

        } catch (Exception e) {
            logger.error("file from POST unsuccessfuly procesed",e);
            return "ERROR";
        }

    }
}
