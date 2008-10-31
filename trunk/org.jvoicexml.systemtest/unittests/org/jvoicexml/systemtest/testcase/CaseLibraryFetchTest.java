package org.jvoicexml.systemtest.testcase;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CaseLibraryFetchTest {

    String docBase = "http://localhost:8080/irtest/irtests/";

    String docURI = docBase + "manifest.xml";

    IRTestCaseLibrary lib;

    @Before
    public void setUp() throws Exception {

        lib = new IRTestCaseLibrary();
        lib.setTestManifest(docURI);
    }

    @Test
    public void listFatchLegal1() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list;

        list = lib.fetch("345");
        iterator = list.iterator();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(345, iterator.next().getId());

        list = lib.fetch("345,346, 1, 2 , 24 ");
        Assert.assertEquals(5, list.size());
        iterator = list.iterator();

        Assert.assertEquals(345, iterator.next().getId());
        Assert.assertEquals(346, iterator.next().getId());
        Assert.assertEquals(1, iterator.next().getId());
        Assert.assertEquals(2, iterator.next().getId());
        Assert.assertEquals(24, iterator.next().getId());

        list = lib.fetch("5");
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void listFatchLegal2() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list;

        list = lib.fetch("345,346, 1-2 , 24 , 11- 18 ");
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

        list = lib.fetch("spec=1 ");
        Assert.assertEquals(67, list.size());
        list = lib.fetch("spec=1.2");
        Assert.assertEquals(9, list.size());
        list = lib.fetch("spec=1.3");
        Assert.assertEquals(18, list.size());
        list = lib.fetch("spec=1.3, spec= 1.2");
        Assert.assertEquals(27, list.size());

    }

    @Test
    public void listRemoveDuplicate() throws Exception {

        Iterator<IRTestCase> iterator;

        Collection<IRTestCase> list;

        // ---------------------------------------------
        list = lib.fetch("7-12, 2-7 , 8 , 18 ");
        Assert.assertEquals(6, list.size());
        iterator = list.iterator();

        Assert.assertEquals(7, iterator.next().getId());
        Assert.assertEquals(8, iterator.next().getId());
        Assert.assertEquals(11, iterator.next().getId());
        Assert.assertEquals(12, iterator.next().getId());
        Assert.assertEquals(2, iterator.next().getId());
        Assert.assertEquals(18, iterator.next().getId());

        list = lib.fetch("spec=1., spec=1, 2 ");
        Assert.assertEquals(67, list.size());

        list = lib.fetch(" 1, 2 , spec= 1,");
        Assert.assertEquals(67, list.size());

        list = lib.fetch(" 1, 2 , spec=1., 7");
        Assert.assertEquals(67, list.size());

        list = lib.fetch("spec=1");
        Assert.assertEquals(67, list.size());

        list = lib.fetch("spec=1.2");
        Assert.assertEquals(9, list.size());

        list = lib.fetch("spec=1, spec=1.2");
        Assert.assertEquals(67, list.size());

    }

    @Test
    public void listFatchIllegal() throws Exception {

        Collection<IRTestCase> list;

        list = lib.fetch("A, aaa,-46, 1- , 11+ 18 ");
        Assert.assertEquals(0, list.size());

    }

}
