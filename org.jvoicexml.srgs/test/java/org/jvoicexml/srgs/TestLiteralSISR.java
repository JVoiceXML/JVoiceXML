package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;

public class TestLiteralSISR {

    @BeforeClass
    public static void loggerSetup() {
        Utils.InitLogger();
    }

    @Test
    public void testLiteralExample() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/Literal1.srgs");
        MatchConsumption mc = parsedGrammar.match("nope");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();

        Assert.assertEquals("no", o);
    }

    @Test
    public void testSingleQuotesInTag() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/Literal2.srgs");
        MatchConsumption mc = parsedGrammar.match("nope");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();

        Assert.assertEquals("no's", o);
    }

}
