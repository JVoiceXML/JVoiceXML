package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;

public class TestExternalRuleref {
    @BeforeClass
    public static void loggerSetup() {
        Utils.initLogger();
    }

    @Test
    public void testExternalWithNamedRule() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/ExternalParent.srgs");
        MatchConsumption mc = parsedGrammar.match("two three dogs");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();

        Assert.assertEquals("23", Utils.getItemOnNativeObject(o, "count"));
        Assert.assertEquals("dog", Utils.getItemOnNativeObject(o, "thing"));
    }

    @Test
    public void testExternalWithDefaultRule() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/ExternalParentDefault.srgs");
        MatchConsumption mc = parsedGrammar.match("two three dogs");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();

        Assert.assertEquals("23", Utils.getItemOnNativeObject(o, "count"));
        Assert.assertEquals("dog", Utils.getItemOnNativeObject(o, "thing"));
    }

}
