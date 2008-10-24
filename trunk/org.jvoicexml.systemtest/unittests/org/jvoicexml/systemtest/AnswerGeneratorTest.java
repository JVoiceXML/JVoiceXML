package org.jvoicexml.systemtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AnswerGeneratorTest {
    // String output345 =
    // "<?xml version=\"1.0\" encoding=\"UTF-8\"?><speak>Press '1'.</speak>";
    AnswerGenerator answerGenerator = null;

    @Before
    public void setUp() {
        answerGenerator = new AnswerGenerator(null);
    }

    @Test
    public void test() throws InterruptedException {
        Assert.assertFalse(answerGenerator.hasMore());

        answerGenerator.outputText("1");
        Assert.assertTrue(answerGenerator.hasMore());

        Assert.assertEquals("1", answerGenerator.waitResult());
        Assert.assertFalse(answerGenerator.hasMore());

        answerGenerator.outputText("1");
        Assert.assertTrue(answerGenerator.hasMore());
        answerGenerator.connected(null);
        Assert.assertFalse(answerGenerator.hasMore());
    }

    @Test
    public void testPass() throws InterruptedException {
        answerGenerator.outputText("   pass   ");
        Assert.assertTrue(answerGenerator.audioResponse());

    }

    @Test
    public void testFail() throws InterruptedException {
        answerGenerator.outputText("   fail   ");
        Assert.assertFalse(answerGenerator.audioResponse());

    }

    @Test
    public void testMessageParse() {
        String answer;
        answer = answerGenerator.parseWord("  '1'  ", "'", "'");
        Assert.assertEquals("1", answer);

        answer = answerGenerator.parseWord("  'aaaa'  ", "'", "'");
        Assert.assertEquals("aaaa", answer);
    }
}
