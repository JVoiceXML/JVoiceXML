package org.jvoicexml.systemtest.log4j;

import java.io.CharArrayWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public class ToStringCollector extends Log4JLogCollector {

    protected final Layout layout = new PatternLayout("%m%n");

    private CharArrayWriter writer = new CharArrayWriter();

    @Override
    public String toString() {
        String message = writer.toString().trim();
        if(message.length() == 0){
            return "-";
        } else {
            return "string:" + writer.toString();
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
