package org.jvoicexml.systemtest.log4j;

import java.io.CharArrayWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public class StringCollector extends Log4JSnoop {

    protected final Layout layout = new PatternLayout("%m%n");

    private CharArrayWriter writer = new CharArrayWriter();

    @Override
    public Object getTrove() {
        String message = writer.toString().trim();
        if (message.length() > 0) {
            return "string:" + writer.toString();
        } else {
            return "";
        }
    }

    @Override
    protected Appender createAppender(String id) {
        writer.reset();
        Appender appender = new WriterAppender(layout, writer);
        appender.setName(id + ".buffer");
        return appender;
    }

}
