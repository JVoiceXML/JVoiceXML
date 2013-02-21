package org.jvoicexml.systemtest.testcase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class URITest {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(URITest.class);

    @Test
    public void testHttp() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/irtest/irtests/manifest.xml");
        Assert.assertFalse(uri.isOpaque());
        Assert.assertTrue(uri.isAbsolute());
        Assert.assertEquals("http", uri.getScheme());
        Assert.assertEquals("//localhost:8080/irtest/irtests/manifest.xml", uri
                .getRawSchemeSpecificPart());
        try {
            uri.toURL();
        } catch (MalformedURLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFileNotAbsolute() throws URISyntaxException {
        URI uri = new URI("file:irtest/irtests/manifest.xml");
        Assert.assertTrue(uri.isOpaque());
        Assert.assertTrue(uri.isAbsolute());
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("irtest/irtests/manifest.xml", uri
                .getRawSchemeSpecificPart());

        try {
            uri.toURL().openStream();
        } catch (MalformedURLException e) {
            Assert.fail();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFileAbsolute() throws URISyntaxException {
        File f = new File("irtest/irtests/manifest.xml");
        Assert.assertTrue(f.exists());
        LOGGER.debug(f.toURI().toString());

        URI uri = f.toURI();
        Assert.assertFalse(uri.isOpaque());
        Assert.assertTrue(uri.isAbsolute());
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertTrue(uri.getRawSchemeSpecificPart().endsWith(
                "irtest/irtests/manifest.xml"));
        try {
            uri.toURL().openStream();
        } catch (MalformedURLException e) {
            Assert.fail();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testNoScheme() throws URISyntaxException {
        URI uri = new URI("config/manifest.xml");
        Assert.assertFalse(uri.isOpaque());
        Assert.assertFalse(uri.isAbsolute());
        Assert.assertNull(uri.getScheme());
        Assert.assertEquals("config/manifest.xml", uri
                .getRawSchemeSpecificPart());
        try {
            uri.toURL();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        } catch (MalformedURLException e) {
            Assert.fail();
        }
    }

}
