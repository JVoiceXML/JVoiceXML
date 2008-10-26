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
        answerGenerator = new AnswerGenerator();
    }

    @Test
    public void test()  {

        Assert.assertEquals("1", answerGenerator.getAnswer("Press '1'"));

        Assert.assertEquals("voice", answerGenerator.getAnswer("Say 'Voice'"));

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