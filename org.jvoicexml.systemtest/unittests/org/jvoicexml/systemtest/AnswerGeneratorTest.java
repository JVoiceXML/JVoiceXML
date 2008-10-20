package org.jvoicexml.systemtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AnswerGeneratorTest {
    String output345 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><speak>Press '1'.</speak>";
    AnswerGenerator answerGenerator = null;
    @Before
    public void setUp(){
        answerGenerator = new AnswerGenerator();
    }
    @Test
    public void test(){
        Assert.assertFalse(answerGenerator.hasMore());
        
        answerGenerator.outputText(output345);
        Assert.assertTrue(answerGenerator.hasMore());
        
        Assert.assertEquals("1", answerGenerator.next());
        
    }
    
    @Test
    public void testPass(){
        answerGenerator.outputText("   pass   ");
        Assert.assertEquals("PASS", answerGenerator.next());
       
    }
    
    @Test
    public void testFail(){
        answerGenerator.outputText("   fail   ");
        Assert.assertEquals("FAIL", answerGenerator.next());
       
    }
}
