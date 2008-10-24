package org.jvoicexml.systemtest;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.systemtest.testcase.IRTestCase;

public class CallManagerTest {

    String docBase = "http://localhost:8080/irtest/irtests/";

    String docURI = docBase + "manifest.xml";
    
    
    SystemTestCallManager cm ;
    

    @Before
    public void setUp() throws Exception {
     
        cm = new SystemTestCallManager();
        cm.setTestManifest(docURI);
    }

    @Test
    public void listFatchLegal1() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list;
        
        list = cm.getJobs("345");
        iterator = list.iterator();
        
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(345, iterator.next().getId());

        list = cm.getJobs("345,346, 1, 2 , 24 ");
        Assert.assertEquals(5, list.size());
        iterator = list.iterator();

        Assert.assertEquals(345, iterator.next().getId());
        Assert.assertEquals(346, iterator.next().getId());
        Assert.assertEquals(1, iterator.next().getId());
        Assert.assertEquals(2, iterator.next().getId());
        Assert.assertEquals(24, iterator.next().getId());

        list = cm.getJobs("5");
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void listFatchLegal2() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list;

        list = cm.getJobs("345,346, 1-2 , 24 , 11- 18 ");
        Assert.assertEquals(8, list.size());
        iterator = list.iterator();
        
        Assert.assertEquals(345, iterator.next().getId());
        Assert.assertEquals(346, iterator.next().getId());
        Assert.assertEquals(1, iterator.next().getId());
        Assert.assertEquals(2, iterator.next().getId());
        Assert.assertEquals(24, iterator.next().getId());
        Assert.assertEquals(11, iterator.next().getId());
        Assert.assertEquals(12, iterator.next().getId());
        Assert.assertEquals(18, iterator.next().getId());
    }

    @Test
    public void listFatchLegal3() throws Exception {

        Collection<IRTestCase> list;

        list = cm.getJobs("spec=1 ");
        Assert.assertEquals(67, list.size());
        list = cm.getJobs("spec=1.2");
        Assert.assertEquals(9, list.size());
        list = cm.getJobs("spec=1.3");
        Assert.assertEquals(18, list.size());
        list = cm.getJobs("spec=1.3, spec= 1.2");
        Assert.assertEquals(27, list.size());

    }

    @Test
    public void listRemoveDuplicate() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list ;

        //---------------------------------------------
        list = cm.getJobs("7-12, 2-7 , 8 , 18 ");
        Assert.assertEquals(6, list.size());
        iterator = list.iterator();
        
        Assert.assertEquals(7, iterator.next().getId());
        Assert.assertEquals(8, iterator.next().getId());
        Assert.assertEquals(11, iterator.next().getId());
        Assert.assertEquals(12, iterator.next().getId());
        Assert.assertEquals(2, iterator.next().getId());
        Assert.assertEquals(18, iterator.next().getId());

        list = cm.getJobs("spec=1., spec=1, 2 ");
        Assert.assertEquals(67, list.size());

        list = cm.getJobs(" 1, 2 , spec= 1,");
        Assert.assertEquals(67, list.size());

        list = cm.getJobs(" 1, 2 , spec=1., 7");
        Assert.assertEquals(67, list.size());

        list = cm.getJobs("spec=1");
        Assert.assertEquals(67, list.size());

        list = cm.getJobs("spec=1.2");
        Assert.assertEquals(9, list.size());

        list = cm.getJobs("spec=1, spec=1.2");
        Assert.assertEquals(67, list.size());

    }

    @Test
    public void listFatchIllegal() throws Exception {

        Collection<IRTestCase> list;

        list = cm.getJobs("A, aaa,-46, 1- , 11+ 18 ");
        Assert.assertEquals(0, list.size());

    }
    
    @Test
    public void testIgnoreWork(){

        
        Collection<IRTestCase> list;
        Iterator<IRTestCase> iterator;
        
        list = cm.getJobs("1", "");
        Assert.assertEquals(1, list.size());
        
        list = cm.getJobs("1", "1");
        Assert.assertEquals(0, list.size());
        
        list = cm.getJobs("1, 2", "1");
        Assert.assertEquals(1, list.size());
        
        iterator = list.iterator();
        Assert.assertEquals(2, iterator.next().getId());
        
    }
}
