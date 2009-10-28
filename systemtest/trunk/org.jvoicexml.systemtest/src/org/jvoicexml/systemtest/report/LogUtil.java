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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
     * @param file the file.
     * @return content of the file.
     * @exception IOException
     *            if the file could not be read
     */
    public static String getContent(final File file) throws IOException {
        if (file == null) {
            throw new IOException("can not read a null file");
        }
        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        final FileInputStream in = new FileInputStream(file);
        int read;
        final byte[] buffer = new byte[1024];
        do {
            read = in.read(buffer);
            if (read > 0) {
                content.write(buffer, 0, read);
            }
        } while (read > 0);
        return content.toString().trim();
    }

    /**
     * @param file the file.
     * @return true if the file exists and include some log.
     */
    public static Boolean exists(final File file) {
        if (file == null) {
            return Boolean.FALSE;
        }
        return file.exists() ? Boolean.TRUE : Boolean.FALSE;
    }
}
