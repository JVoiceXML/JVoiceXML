package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.SrgsSisrGrammar;

public class TestMatching {
    SrgsSisrGrammar parsedGrammar = null;

    @BeforeClass
    public static void loggerSetup() {
        Utils.InitLogger();
    }

    @Before
    public void loadGrammar() throws Exception {
        parsedGrammar = Utils
                .loadDocument("staticTestFiles/MediumGrammar.srgs");
        parsedGrammar.dump();
    }

    @Test
    public void phrases1() {
        MatchConsumption mc = parsedGrammar.match("two three dogs");
        Assert.assertNotNull(mc);
    }

    @Test
    public void phrases2() {
        MatchConsumption mc = parsedGrammar.match("i want two three dogs");
        Assert.assertNotNull(mc);
    }

    @Test
    public void phrases3() {
        MatchConsumption mc = parsedGrammar.match("two three four cats");
        Assert.assertNotNull(mc);
    }

    @Test
    public void phrasesNoMatch() {
        MatchConsumption mc = parsedGrammar.match("alpha two three four cats");
        Assert.assertNull(mc);
    }

}
