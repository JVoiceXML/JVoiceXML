package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class ReportXsltTest {
    final static private Logger LOGGER = Logger.getLogger(ReportXsltTest.class);

    String rootPath = "irtest/xslt";
    String relativelyPath = "ir-report.xml";
    String xslt = "ir.xslt";

    @Test
    public void createReport() {

        try {

            File dataFile = toFile(rootPath, relativelyPath);
            File xsltFile = toFile(rootPath, xslt);

            OutputStream outStream = System.out;
            InputStream dataStream = new FileInputStream(dataFile);
            InputStream styleStream = new FileInputStream(xsltFile);
            transfer(outStream, dataStream, styleStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private File toFile(String root, String relativelyPath) throws IOException {
        LOGGER.debug("path = " + relativelyPath);

        if (relativelyPath == null) {
            throw new IOException("file name is null.");
        }

        File file = new File(root, relativelyPath);

        return file;
    }

    private void transfer(OutputStream out, InputStream dataStream,
            InputStream styleStream) throws IOException, TransformerException {

        Source data = new StreamSource(dataStream);
        Source style = new StreamSource(styleStream);
        Result output = new StreamResult(out);

        Transformer xslt = TransformerFactory.newInstance().newTransformer(
                style);
        xslt.transform(data, output);
    }

}
