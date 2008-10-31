package org.jvoicexml.systemtest.script;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.systemtest.script.Action;
import org.jvoicexml.systemtest.script.Script;
import org.jvoicexml.systemtest.script.ScriptFactory;

public class ScriptFactoryTest {

    String path = "irtest/scripts/";
    

    @Test
    public void test() {
        ScriptFactory factory = new ScriptFactory(path);

        Script script7 = factory.create("7");
        Assert.assertNotNull(script7);

        Collection<Action> list = script7.getActions();

        Assert.assertTrue(list instanceof LinkedList);

        Assert.assertEquals(3, list.size());
    }
    
    @Test
    public void scriptLoad() throws Exception {

        ScriptFactory factory = new ScriptFactory(path);
        
        URL file = ScriptDocNodeTest.class.getResource("example.script.xml");
        
        Script script = factory.create(file.openStream());

        Assert.assertEquals(3, script.getActions().size());
        Iterator<Action> iterator = script.getActions().iterator();

        Action a = iterator.next();
        Assert.assertTrue(a instanceof WaitAction);
        Assert.assertEquals(WaitAction.DEFAULT_WAIT_TIME,
                ((WaitAction) a).timeout);

        a = iterator.next();
        Assert.assertTrue(a instanceof WaitAction);
        Assert.assertEquals(2000, ((WaitAction) a).timeout);

        a = iterator.next();
        Assert.assertTrue(a instanceof AnswerAction);
        Assert.assertEquals("That is OK.", ((AnswerAction) a).speak);
    }

}
