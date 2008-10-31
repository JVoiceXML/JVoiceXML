package org.jvoicexml.systemtest.log4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class FileCollector extends Log4JSnoop {

    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(FileCollector.class);

    String reportLocation = null;

    String suffix = null;

    File writrTo = null;

    protected final Layout layout = new PatternLayout(
            "%6r [%-20.20t] %-5p %30.30c (%6L) %x %m%n");

    @Override
    public URI getTrove() {
        URI docRoot = new File(reportLocation).toURI();
        URI fileUri = writrTo.toURI();
        String schema = fileUri.getScheme();
        String relative = docRoot.relativize(writrTo.toURI()).toString();
        URI result = null;
        try {
            result = new URI(schema, relative, null);
        } catch (URISyntaxException e) {
        }
        return result;
    }

    @Override
    protected Appender createAppender(String id) {
        createDirectory(reportLocation);

        File docRoot = new File(reportLocation);
        String name = id + "." + suffix;
        writrTo = new File(docRoot, name);
        Appender appender = null;

        try {
            appender = new FileAppender(layout, writrTo.getAbsolutePath(),
                    false);
            appender.setName(name);
        } catch (IOException e) {
            LOGGER.error("FileAppender create error. ", e);
        }
        return appender;
    }

    public void setReportLocation(String location) {

        reportLocation = location;

    }

    public void setLogFileSuffix(String suffix) {
        this.suffix = suffix;
    }

    private void createDirectory(String reportLocation) {
        File dir = new File(reportLocation);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println(dir.getAbsolutePath()
                        + " not exists, and can not create dirs.");
            }
            return;
        }
        if (!dir.isDirectory()) {
            if (!dir.delete()) {
                System.out.println(dir.getAbsolutePath()
                        + " is not directory, and can not delete.");
                return;
            }
            dir.mkdirs();
            return;
        }
        if (!dir.canWrite()) {
            System.out.println(dir.getAbsolutePath()
                    + " can not have write permission.");
        }
    }

}
