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
package org.jvoicexml.systemtest.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * log utility class.
 * @author lancer
 *
 */
public final class LogUtil {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(LogUtil.class);

    /**
     * hide construct method.
     */
    private LogUtil() {
    }

    /**
     * @param base file base.
     * @param page the file.
     * @return page relative URI about base.
     */
    public static URI getURI(final File base, final File page) {
        if (base == null || page == null) {
            return null;
        }
        URI docRoot = base.toURI();
        URI fileUri = page.toURI();
        String schema = fileUri.getScheme();
        String relative = docRoot.relativize(page.toURI()).toString();

        try {
            return new URI(schema, relative, null);
        } catch (URISyntaxException e) {
            LOGGER.warn("URISyntaxException", e);
            return null;
        }
    }


    /**
     * @param arg0 the file.
     * @return content of the file.
     */
    public static String getContent(final File arg0) {
        if (arg0 == null) {
            return "file not found.";
        }
        final StringBuffer buff = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arg0));
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line);
                buff.append("\n");
            }
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
        }
        return buff.toString().trim();
    }

    /**
     * @param arg0 the file.
     * @return true if the file exists and include some log.
     */
    public static Boolean isExists(final File arg0) {
        if (arg0 == null) {
            return Boolean.FALSE;
        }
        String result = getContent(arg0);
        return result.length() > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
