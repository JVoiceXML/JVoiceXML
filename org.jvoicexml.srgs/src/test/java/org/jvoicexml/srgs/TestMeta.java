package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;

public class TestMeta {

    @BeforeClass
    public static void loggerSetup() {
        Utils.initLogger();
    }

    @Test
    public void testLatest() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("/metaCurrentText.srgs");

        MatchConsumption mc = parsedGrammar.match("one two");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("one", Utils.getItemOnNativeObject(o, "firstDigit"));
        Assert.assertEquals("one two",
                Utils.getItemOnNativeObject(o, "secondDigit"));
        Assert.assertEquals("one two", Utils.getItemOnNativeObject(o, "all"));
    }

}
