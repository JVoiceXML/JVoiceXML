package org.jvoicexml.srgs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.ItemRuleExpansion;
import org.jvoicexml.srgs.SrgsRule;
import org.jvoicexml.srgs.SrgsSisrGrammar;

/** A series of test cases against the MediumGrammar.srgs file **/
public class TestSrgsSisrXmlGrammarParser {
    SrgsSisrGrammar parsedGrammar = null;

    @BeforeClass
    public static void loggerSetup() {
        Utils.initLogger();
    }

    @Before
    public void loadGrammar() throws Exception {
        parsedGrammar = Utils
                .loadDocument("unittests/staticTestFiles/MediumGrammar.srgs");
        parsedGrammar.dump();
    }

    @Test
    public void testGlobalTagParse() {
        Assert.assertEquals(
                "var global1 = 'alpha';\nvar global2 = \"beta\";\nvar global3 = 'gammar';\n",
                parsedGrammar.getGlobalTags().getCurrentText());
    }

    @Test
    public void testRulesExistence() {
        Assert.assertEquals(5, parsedGrammar.getRules().size());
        Assert.assertNotNull(parsedGrammar.getRule("choice", false));
        Assert.assertNotNull(parsedGrammar.getRule("filler", false));
        Assert.assertNotNull(parsedGrammar.getRule("things", false));
        Assert.assertNotNull(parsedGrammar.getRule("dig2_3", false));
        Assert.assertNotNull(parsedGrammar.getRule("Digits", false));
    }

    @Test
    public void testFillerGrammar() {
        SrgsRule rule = parsedGrammar.getRule("filler", false);
        Assert.assertEquals("filler", rule.getId());
        Assert.assertEquals(true, rule.isPublic());
        Assert.assertNotNull(rule.getInnerRule());
    }

    @Test
    public void testChoiceGrammar() {
        SrgsRule rule = parsedGrammar.getRule("choice", false);
        Assert.assertEquals("choice", rule.getId());
        Assert.assertEquals(true, rule.isPublic());
        Assert.assertNotNull(rule.getInnerRule());
    }

    @Test
    public void testChoiceSubItemGrammar() {
        SrgsRule rule = parsedGrammar.getRule("choice", false);
        Assert.assertEquals("choice", rule.getId());
        Assert.assertNotNull(rule.getInnerRule());

        Assert.assertTrue(rule.getInnerRule() instanceof ItemRuleExpansion);
        ItemRuleExpansion item = (ItemRuleExpansion) rule.getInnerRule();

        Assert.assertEquals(3, item.getSubItems().size());

        ItemRuleExpansion subitem = (ItemRuleExpansion) (item.getSubItems()
                .get(0));
        Assert.assertEquals(0, subitem.getMinRepeat());
        Assert.assertEquals(1, subitem.getMaxRepeat());
    }

}
