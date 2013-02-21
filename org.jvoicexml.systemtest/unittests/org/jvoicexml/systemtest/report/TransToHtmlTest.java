package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TransToHtmlTest {
    final static private Logger LOGGER = Logger
            .getLogger(TransToHtmlTest.class);

    @Test
    public void test() throws Exception {

        InputStream data = new FileInputStream(new File(
                "irtest/results/ir-report.xml"));
        InputStream style = new FileInputStream(new File(
                "irtest/results/ir.xslt"));
        File out = new File("irtest/results/ir-report.html");
        if (out.exists()) {
            out.delete();
        }
        OutputStream html = new FileOutputStream(out);

        transfer(html, data, style);

    }

    private void transfer(final OutputStream out, final InputStream dataStream,
            final InputStream styleStream) throws IOException,
            TransformerException {

        Source data = new StreamSource(dataStream);
        Source style = new StreamSource(styleStream);
        Result output = new StreamResult(out);

        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setErrorListener(new MyListener());
        LOGGER.debug(factory.getClass().getName());

        Transformer xslt = factory.newTransformer(style);
        xslt.transform(data, output);
    }

    class MyListener implements ErrorListener {

        @Override
        public void error(final TransformerException arg0)
                throws TransformerException {
            LOGGER.error("TransformerException", arg0);
        }

        @Override
        public void fatalError(final TransformerException arg0)
                throws TransformerException {
            LOGGER.fatal("TransformerException", arg0);

        }

        @Override
        public void warning(final TransformerException arg0)
                throws TransformerException {
            LOGGER.warn("TransformerException", arg0);

        }
    }
}
