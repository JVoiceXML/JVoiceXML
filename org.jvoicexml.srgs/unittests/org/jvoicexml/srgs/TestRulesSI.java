package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;

public class TestRulesSI {

    @BeforeClass
    public static void loggerSetup() {
        Utils.InitLogger();
    }

    @Test
    public void testLatest() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/rulesLatest.srgs");

        MatchConsumption mc = parsedGrammar.match("one");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals((double) 1.0, o);
    }

    @Test
    public void testRuleByName() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/rulesLatest.srgs");

        MatchConsumption mc = parsedGrammar.match("one");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals((double) 1.0, o);
    }

}
