package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** Most SI tests */
public class TestOneOfRuleRef {
    @BeforeClass
    public static void loggerSetup() {
        Utils.initLogger();
    }

    

    @Test
    public void testDefaultAssignment4No() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("/defaultAssignment4.srgs");
        MatchConsumption mc = parsedGrammar.match("nope");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("no", o);
    }
    
    @Test
    public void testDefaultAssignment4Yeah() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("/defaultAssignment4.srgs");
        MatchConsumption mc = parsedGrammar.match("yes");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("yes", o);
    }
    
    @Test
    public void testDefaultAssignment4AhNope() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("/defaultAssignment4.srgs");
        MatchConsumption mc = parsedGrammar.match("ah nope");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("no", o);
    }

}
