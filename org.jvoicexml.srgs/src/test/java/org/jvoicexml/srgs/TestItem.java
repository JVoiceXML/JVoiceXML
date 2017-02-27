package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.srgs.ItemRuleExpansion;
import org.jvoicexml.srgs.MatchConsumption;
import org.jvoicexml.srgs.TokenRuleExpansion;

public class TestItem {
    @BeforeClass
    public static void loggerSetup() {
        Utils.initLogger();
    }

    @Test
    public void matchItemWithOneToken() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getTokensConsumed());
    }

    @Test
    public void matchItemWithTwoTokens() {
        TokenRuleExpansion token1 = createToken(new String[] { "dog", "cat",
                "cow" });
        TokenRuleExpansion token2 = createToken(new String[] { "red", "white",
                "blue" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token1);
        item.addSubRule(token2);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow", "red", "white", "blue" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getTokensConsumed());
    }

    @Test
    public void noMatchItemWithTwoTokens() {
        TokenRuleExpansion token1 = createToken(new String[] { "dog", "cat",
                "cow" });
        TokenRuleExpansion token2 = createToken(new String[] { "red", "white",
                "blue" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token1);
        item.addSubRule(token2);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow", "red", "wrong", "blue" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNull(result);
    }

    @Test
    public void matchItemWithZeroOneRepeat_OneMatch() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);
        item.setRepeat(0, 1);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getTokensConsumed());
    }

    @Test
    public void matchItemWithZeroOneRepeat_ZeroMatch() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);
        item.setRepeat(0, 1);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "wrong", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getTokensConsumed());
    }

    @Test
    public void matchItemWithTwoThreeRepeat_ThreeMatch() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);
        item.setRepeat(2, 3);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow", "dog", "cat", "cow", "dog", "cat", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(9, result.getTokensConsumed());
    }

    @Test
    public void matchItemWithTwoThreeRepeat_TwoMatch() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);
        item.setRepeat(2, 3);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow", "dog", "cat", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getTokensConsumed());
    }

    @Test
    public void NoMatchItemWithTwoThreeRepeat_OneMatch() {
        TokenRuleExpansion token = createToken(new String[] { "dog", "cat",
                "cow" });
        ItemRuleExpansion item = new ItemRuleExpansion();
        item.addSubRule(token);
        item.setRepeat(2, 3);

        ArrayList<String> input = createStringArrayList(new String[] { "dog",
                "cat", "cow" });
        MatchConsumption result = item.match(input, 0);

        Assert.assertNull(result);
    }

    private TokenRuleExpansion createToken(String[] tokens) {
        TokenRuleExpansion token = new TokenRuleExpansion();
        for (String s : tokens)
            token.addToken(s);
        return token;
    }

    private ArrayList<String> createStringArrayList(String[] tokens) {
        ArrayList<String> result = new ArrayList<String>();
        for (String s : tokens)
            result.add(s);
        return result;
    }
}
