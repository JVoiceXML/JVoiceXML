package org.jvoicexml.systemtest.log4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class URITest {
    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(URITest.class);

    @Test
    public void test(){
        LOGGER.debug("------------------------------- test()");
        try {
            URI uri = new URI("file:" + "a.txt");
            LOGGER.debug(uri.toString());
        } catch (URISyntaxException e) {
            Assert.fail();
        }
        try {
            URI uri = new URI("file:" + "/aaa/bbb/a.txt");
            LOGGER.debug(uri.toString());
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }
    @Test
    public void testFileToUri() throws MalformedURLException, URISyntaxException{
        LOGGER.debug("------------------------------- testFileToUri()");
        File f = new File("testFile");
        LOGGER.debug(f.toURI().toString());
        
        LOGGER.debug(f.toURI().toURL().toString());
        
        LOGGER.debug(f.toURI().toURL().toURI().toString());
    }
    
    @Test
    public void testURIRelativize() throws MalformedURLException, URISyntaxException{
        LOGGER.debug("------------------------------- testURIRelativize()");
        File f = new File("testFile");
        File dir = new File(".");
        URI root = dir.toURI();
        LOGGER.debug("root : " + root.toString());
        
        URI file = f.toURI();
        LOGGER.debug("file : " + file.toString());
        
        LOGGER.debug(root.relativize(file).toString());
        
        LOGGER.debug(file.relativize(root).toString());
    }
}
