/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO insert comment
 * @author Dwalka
 * @since 0.7.9
 */
public class JVoiceXmlClassLoaderTest {
    private JVoiceXmlClassLoader loader;
    
    /**
     * Setup the test environment.
     */
    @Before
    public void setUp() throws Exception {
        final ClassLoader parent = getClass().getClassLoader();
        loader = new JVoiceXmlClassLoader(parent);
        final File file = new File("src/test/extresources/org.jvoicexml.dummy.jar");
        if (!file.exists()) {
            throw new FileNotFoundException(file.getCanonicalPath());
        }
        final URL url = file.toURI().toURL();
        loader.addURL(url);
    }

    /**
     * Test method for {@link java.net.URLClassLoader#findResource(String)}.
     * @throws ClassNotFoundException 
     */
    @Test
    public void testLoadClass() throws ClassNotFoundException {
        loader.loadClass("org.jvoicexml.Application");
    }

    /**
     * Test method for {@link java.net.URLClassLoader#findResource(String)}.
     * @throws ClassNotFoundException 
     */
    @Test
    public void testLoadClasExternal() throws ClassNotFoundException {
        loader.loadClass("org.jvoicexml.dummy.Dummy");
    }
    
    /**
     * Test method for {@link java.net.URLClassLoader#getResourceAsStream(java.lang.String)}.
     */
    @Test
    public void testGetResourceAsStream() {
        final InputStream in = loader.getResourceAsStream("org/jvoicexml/Application.class");
        Assert.assertNotNull(in);
    }

    /**
     * Test method for {@link java.net.URLClassLoader#getResourceAsStream(java.lang.String)}.
     */
    @Test
    public void testGetResourceAsStreamExternal() {
        final InputStream in = loader.getResourceAsStream("org/jvoicexml/dummy/Dummy.class");
        Assert.assertNotNull(in);
    }

    /**
     * Test method for {@link java.net.URLClassLoader#getResourceAsStream(java.lang.String)}.
     * @throws InterruptedException 
     */
    @Test
    public void testGetResourceAsStreamAsync() throws InterruptedException {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final InputStream in = loader.getResourceAsStream("org/jvoicexml/Application.class");
                Assert.assertNotNull(in);
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        synchronized (runnable) {
            runnable.wait();
        }
    }

    /**
     * Test method for {@link java.net.URLClassLoader#getResourceAsStream(java.lang.String)}.
     * @throws InterruptedException 
     */
    @Test
    public void testGetResourceAsStreamExternalAsync() throws InterruptedException {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final InputStream in = loader.getResourceAsStream("org/jvoicexml/dummy/Dummy.class");
                Assert.assertNotNull(in);
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        synchronized (runnable) {
            runnable.wait();
        }
    }

    /**
     * Tear down the test environment
     * @throws Exception
     *          error tearing down the environment
     */
    @After
    public void tearDown() throws Exception {
        loader.close();
    }
}
