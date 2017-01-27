package org.jvoicexml.srgs;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;
import org.mozilla.javascript.Scriptable;

/** Most SI tests */
public class TestMatchConsumptionSisr {
    @BeforeClass
    public static void loggerSetup() {
        Utils.InitLogger();
    }

    @Test
    public void testMediumGrammar() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/MediumGrammar.srgs");
        MatchConsumption mc = parsedGrammar.match("two three dogs");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();

        Assert.assertEquals("23", Utils.getItemOnNativeObject(o, "count"));
        Assert.assertEquals("dog", Utils.getItemOnNativeObject(o, "thing"));
    }

    @Test
    public void testDefaultAssignment1() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/defaultAssignment1.srgs");
        MatchConsumption mc = parsedGrammar.match("two");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Logger.getRootLogger().warn(
                "o=" + o + " is a " + o.getClass().getCanonicalName());
        if (o instanceof Scriptable)
            Utils.dumpScope((Scriptable) o);

        Assert.assertEquals("two", o);
    }

    @Test
    public void testDefaultAssignment2() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/defaultAssignment2.srgs");
        MatchConsumption mc = parsedGrammar.match("two");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("two", o);
    }

    @Test
    public void testDefaultAssignment3() throws Exception {
        SrgsSisrGrammar parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/defaultAssignment3.srgs");
        MatchConsumption mc = parsedGrammar.match("nope");
        Assert.assertNotNull(mc);

        Object o = mc.executeSisr();
        Assert.assertEquals("no", o);
    }

}
