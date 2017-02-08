package hr.altima.cron;

import hr.altima.services.XmlProcessorService;
import hr.altima.util.XmlProcessorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

/**
 * Created by simon on 3.2.2017.
 */
@Component
public class XmlProcessorScheduler {

    private static final Logger logger = LoggerFactory.getLogger(XmlProcessorScheduler.class);

    @Autowired
    XmlProcessorService xmlProcessorService;

    @Value("${file.processing.input.folder}")
    private String inputFolder;

    @Value("${file.processing.output.folder}")
    private String outputFolder;

    @Value("${file.processing.xpath.query}")
    private String xpathQuery;

    @Scheduled(fixedDelayString = "${file.processing.interval.seconds:60}000")
    public void fixedDelayTask() {

        logger.info("Started procesing xml-files from " + inputFolder);
        List<File> filesToProces = xmlProcessorService.getFilesToProcess(inputFolder);
        for (File file : filesToProces) {
            String filename = file.getName();
            try {

                Document input = XmlProcessorUtils.getDocumentFromFile(file);
                String outFilename = filename.replace(".xml",  "_" + System.currentTimeMillis() + ".xml") ;

                Document output = xmlProcessorService.processXmlDocument(input, xpathQuery);
                xmlProcessorService.createOutputFile(output, outputFolder + "/" + outFilename);
                xmlProcessorService.deleteInput(inputFolder + "/" + filename);
                xmlProcessorService.createOrUpdateReport(outFilename, input.getDocumentElement().getNodeName());
                logger.info("File " + filename + " successfuly processed to output file " + filename);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Error during processing " + inputFolder + "/" + filename);
            }

        }
        logger.info("Finished procesing xml-files from " + inputFolder);
    }




}
