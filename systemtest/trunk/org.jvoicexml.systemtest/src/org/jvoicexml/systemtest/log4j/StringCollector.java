/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest.log4j;

import java.io.CharArrayWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * collect log to String.
 * @author lancer
 *
 */
public class StringCollector extends Log4JSnoop {

    /**
     * output format layout.
     */
    protected final Layout layout = new PatternLayout("%m%n");

    /**
     * the appender writer.
     */
    private CharArrayWriter writer = new CharArrayWriter();

    /**
     * (non-Javadoc)
     * @see org.jvoicexml.systemtest.LogSnoop#getTrove()
     */
    @Override
    public Object getTrove() {
        String message = writer.toString().trim();
        if (message.length() > 0) {
            return "string:" + writer.toString();
        } else {
            return "";
        }
    }

    /**
     * (non-Javadoc)
     * @see org.jvoicexml.systemtest.log4j.Log4JSnoop#createAppender(java.lang.String)
     */
    @Override
    protected Appender createAppender(String id) {
        writer.reset();
        Appender appender = new WriterAppender(layout, writer);
        appender.setName(id + ".buffer");
        return appender;
    }

}
