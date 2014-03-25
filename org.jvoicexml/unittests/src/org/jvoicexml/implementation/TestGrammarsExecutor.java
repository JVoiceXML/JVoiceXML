/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation;

import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.mock.MockRecognitionResult;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;


/**
 *
 * @author raphael
 */
public final class TestGrammarsExecutor {
    
    final String utterance = "accept";
    final SrgsXmlDocument doc;
    final GrammarImplementation grammar;
    final GrammarsExecutor exec;
            
    MockRecognitionResult result;
    
    public TestGrammarsExecutor() throws ParserConfigurationException {
        doc = new SrgsXmlDocument();
        doc.setGrammarSimple("test", utterance);
        grammar = new SrgsXmlGrammarImplementation(doc);
        exec = new GrammarsExecutor();
        exec.getSet().add(grammar);
    }
    
    @Before
    public void setUp() {
        result = new MockRecognitionResult();
    }

    @Test
    public void shouldAddGrammar() {
        Assert.assertFalse(exec.getSet().isEmpty());
    }

    @Test
    public void shouldRemoveGrammar() {
        exec.getSet().remove(grammar);
        Assert.assertTrue(exec.getSet().isEmpty());
    }
    
    @Test
    public void shouldAccept() {
        result.setUtterance(utterance);
        Assert.assertTrue(exec.isAcceptable(result));
        Assert.assertEquals(exec.getLastGrammar(), grammar);
    }
    
    @Test
    public void shouldReject() {
        Assert.assertFalse(exec.isAcceptable(result));
        Assert.assertNull(exec.getLastGrammar());
    }
}
