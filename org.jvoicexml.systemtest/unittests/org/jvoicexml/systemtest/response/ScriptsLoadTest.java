package org.jvoicexml.systemtest.response;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ScriptsLoadTest {

    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(ScriptsLoadTest.class);
    
    String filePath = "irtest/scripts/ignores.xml";

    @Test
    public void load() throws Exception {
        URL file = new File(filePath).toURI().toURL();
        ScriptsNode script = ScriptsNode.load(file.openStream());

        Assert.assertEquals(114, script.getList().size());
        Iterator<ScriptDocNode> iterator = script.getList().iterator();

        ScriptDocNode a = iterator.next();
        Assert.assertEquals("1025", a.id);

    }
}
