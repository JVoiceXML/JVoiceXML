package org.jvoicexml.systemtest.testcase;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IRTestsLoadTest {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(IRTestsLoadTest.class);
    
    IRTestCaseLibrary lib = null;
    
    String docBase = "http://localhost:8080/irtest/irtests/";

    String docURI = docBase + "manifest.xml";
    
    @Before
    public void setUp()  throws Exception {
        boolean remote = true;
        URL url = null;
        if (remote) {
            url = new URL(docURI);
        } else {
            File f = new File(
                    "/home/lancer/works/nsjvxml/xxjas/xxjas-vxml/vxml/irtests/manifest.xml");
            Assert.assertTrue(f.exists());
            url = f.toURI().toURL();
        }
        lib = new IRTestCaseLibrary(url);
    }

    @Test
    public void testLoad(){
        

        LOGGER.debug("total test case = " + lib.size());
        Assert.assertTrue(lib.size() > 0);
        
        IRTestCase tc = lib.fetchAll().get(0);
        LOGGER.debug("id = " + tc.id);
        LOGGER.debug("desc = " + tc.description);

        IRTestCase.Description desc = tc.description;

        LOGGER.debug("id = " + desc.id);
        LOGGER.debug("spec = " + desc.spec);
        LOGGER.debug("text = " + desc.text);
    }
    
    @Test
    public void testTestCaseStartURI() throws Exception {

        Assert.assertTrue(lib.size() > 0);
        IRTestCase tc = lib.fetchAll().get(0);
        URI start = tc.getStartURI();
        LOGGER.debug("case 0 start uri string : " + start);
        
        URI base = lib.getBaseUri();
        LOGGER.debug("base uri : " + base.toString());
        
        Assert.assertEquals(docBase + "1/1.txml", start.toString());
        Assert.assertTrue(tc.hasDeps());
    }
    
    @Test
    public void testURIResolve() throws Exception{
        URI base1 = new URI("http://localhost:8080/jvxml/irtests");
        URI base2 = new URI("http://localhost:8080/jvxml/irtests/");
        Assert.assertNotSame(base1.toString(), base2.toString());
        
        URI a1, a2;
        
        a1 = base1.resolve("a"); 
        a2 = base2.resolve("a"); 
        Assert.assertEquals("http://localhost:8080/jvxml/a", a1.toString());
        Assert.assertEquals("http://localhost:8080/jvxml/irtests/a", a2.toString());
        
        a1 = base1.resolve("/a"); 
        a2 = base2.resolve("/a"); 
        Assert.assertEquals("http://localhost:8080/a", a1.toString());
        Assert.assertEquals("http://localhost:8080/a", a2.toString());
        
        a1 = base1.resolve("."); 
        a2 = base2.resolve("."); 
        Assert.assertEquals("http://localhost:8080/jvxml/", a1.toString());
        Assert.assertEquals("http://localhost:8080/jvxml/irtests/", a2.toString());
    }
    
    @Test
    public void listTestCase() throws Exception {

        Assert.assertTrue(lib.size() > 0);
        for(IRTestCase tc : lib.fetchAll()){
            LOGGER.debug("case " + tc.toString());
            LOGGER.debug("start uri = " + tc.getStartURI());
        }
        LOGGER.debug("Total have " + lib.size() + " test cases.");
    }
    
    @Test
    public void testServletBaseURI()  throws Exception {

        URL url = null;

        url = new URL(docBase + "manifest.xml");
        IRTestCaseLibrary lib = new IRTestCaseLibrary(url);
        Assert.assertTrue(lib.size() > 0);
        IRTestCase tc = lib.fetch(1);
        Assert.assertEquals(docBase + tc.start.uri, tc.getStartURI().toString());
    }
}
