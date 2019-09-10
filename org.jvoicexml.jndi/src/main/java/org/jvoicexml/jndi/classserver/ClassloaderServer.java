/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.jndi.classserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassloaderServer extends ClassServer {

    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(ClassloaderServer.class);

    /** The class loader to load the requested class files. */
    private final ClassLoader loader;

    /**
     * Constructs a ClassFileServer.
     *
     * @param port
     *            the server port
     */
    public ClassloaderServer(int port) throws IOException {
        super(port);
        loader = getClass().getClassLoader();
        LOGGER.info("using JNDI class loader '" + loader + "'");
    }

    /**
     * Retrieves an input stream to read the resource at the given path.
     * @param path the path of the resource to load
     * @return associated input stream, {@code null} if the resource cannot be
     * loaded.
     */
    public InputStream getInputStream(final String path) {
        String loaderPath = path.replace('.', '/') + ".class";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading " + loaderPath);
        }
        InputStream in = loader.getResourceAsStream(loaderPath);
        if (in == null) {
            in = loader.getResourceAsStream('/' + loaderPath);
        }
        if (in == null) {
            in = getClass().getResourceAsStream('/' + loaderPath);
        }
        return in;
    }

    @Override
    public byte[] getBytes(String path)
            throws IOException, ClassNotFoundException {
        final InputStream in = getInputStream(path);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (in == null) {
            LOGGER.warn("unable to load '" + path + "' from " + loader);
            throw new IOException("unable to load '" + path + "'");
        }
        final byte[] bytes = new byte[1024];
        int read = 0;;
        do
        {
            read = in.read(bytes);
            if (read > 0) {
                out.write(bytes, 0, read);
            }
        } while (read > 0);
        return out.toByteArray();
    }

    @Override
    public void run() {
        Thread thread = Thread.currentThread();
        thread.setContextClassLoader(loader);
        super.run();
    }
}
