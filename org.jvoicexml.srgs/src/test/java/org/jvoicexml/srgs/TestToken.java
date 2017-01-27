package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.TokenRuleExpansion;

public class TestToken {
    @BeforeClass
    public static void loggerSetup() {
        Utils.InitLogger();
    }

    @Test
    public void testNoMatchSameSize1() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("dog");
        tokens.add("moo");
        tokens.add("bear");
        Assert.assertNull("Should not have matched", token.match(tokens, 0));
    }

    @Test
    public void testNoMatchSmall() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("dog");
        tokens.add("bear");
        Assert.assertNull("Should not have matched", token.match(tokens, 0));
    }

    @Test
    public void testNoMatchLarge() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("alpha");
        tokens.add("dog");
        tokens.add("moo");
        tokens.add("bear");
        Assert.assertNull("Should not have matched", token.match(tokens, 0));
    }

    @Test
    public void testMatch() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("dog");
        tokens.add("cow");
        tokens.add("bear");
        MatchConsumption result = token.match(tokens, 0);
        Assert.assertNotNull("Should have matched", result);
        Assert.assertEquals(3, result.getTokensConsumed());
        Assert.assertArrayEquals(tokens.toArray(), result.getTokens().toArray());
    }

    @Test
    public void testMatchWithOffset() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("alpha");
        tokens.add("dog");
        tokens.add("cow");
        tokens.add("bear");

        ArrayList<String> shouldMatch = new ArrayList<String>();
        shouldMatch.add("dog");
        shouldMatch.add("cow");
        shouldMatch.add("bear");

        MatchConsumption result = token.match(tokens, 1);
        Assert.assertNotNull("Should have matched", result);
        Assert.assertEquals(3, result.getTokensConsumed());
        Assert.assertArrayEquals(shouldMatch.toArray(), result.getTokens()
                .toArray());
    }

    @Test
    public void testNoMatchWithOffset1() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("alpha");
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("dog");
        tokens.add("cow");
        tokens.add("bear");
        MatchConsumption result = token.match(tokens, 0);
        Assert.assertNull("Should not have matched", result);
    }

    @Test
    public void testNoMatchWithOffset2() {
        TokenRuleExpansion token = new TokenRuleExpansion();
        token.addToken("alpha");
        token.addToken("dog");
        token.addToken("cow");
        token.addToken("bear");

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("dog");
        tokens.add("cow");
        tokens.add("bear");
        MatchConsumption result = token.match(tokens, 2);
        Assert.assertNull("Should not have matched", result);
    }
}
