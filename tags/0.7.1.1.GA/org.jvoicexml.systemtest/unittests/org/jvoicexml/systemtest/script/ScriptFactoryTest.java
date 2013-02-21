package org.jvoicexml.systemtest.script;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ScriptFactoryTest {

    String path = "irtest/scripts/";
    
    InputScriptFactory factory ;
    
    @Before
    public void setUp(){
        factory = new InputScriptFactory(path);
    }
    

    @Test
    public void test() {
        

        InputScript script7 = factory.create("7");
        Assert.assertNotNull(script7);

        Collection<Action> list = script7.getActions();

        Assert.assertTrue(list instanceof LinkedList);

        Assert.assertEquals(1, list.size());
    }
    
    @Test
    public void scriptLoad() throws Exception {
        
        URL file = ScriptDocNodeTest.class.getResource("example.script.xml");
        
        InputScript script = factory.create(file.openStream());

        Assert.assertEquals(3, script.getActions().size());
        Iterator<Action> iterator = script.getActions().iterator();

    }

}
