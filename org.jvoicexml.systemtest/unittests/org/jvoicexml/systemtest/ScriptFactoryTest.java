package org.jvoicexml.systemtest;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.systemtest.response.Script;


public class ScriptFactoryTest {

    String path = "irtest/scripts/";
    
    @Test
    public void test(){
        ScriptFactory factory = new ScriptFactory (path);

        Script script7 = factory.create("7");
        Assert.assertNotNull(script7);
        
        Collection<Action> list = script7.getActions();
        
        Assert.assertTrue(list instanceof LinkedList);
        
        
        Assert.assertEquals(3, list.size());
    }

}
