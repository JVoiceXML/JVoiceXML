/**
 * 
 */
package org.jvoicexml.implementation.mrcpv2;

import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Test class for 
 * {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation}.
 */
public class Mrcpv2GrammarImplementationTest {

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#getMediaType()}.
     */
    @Test
    public void testGetMediaType() {
        final GrammarType type = GrammarType.JSGF;
        final Mrcpv2GrammarImplementation<String> grammar = 
                new Mrcpv2GrammarImplementation<String>(null, type, null);
        Assert.assertEquals(type, grammar.getMediaType());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#getModeType()}.
     */
    @Test
    public void testGetModeType() {
        final ModeType type = ModeType.VOICE;
        final Mrcpv2GrammarImplementation<String> grammar = 
                new Mrcpv2GrammarImplementation<String>(null, null, type);
        Assert.assertEquals(type, grammar.getModeType());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#getGrammarDocument()}.
     */
    @Test
    public void testGetGrammarDocument() {
        final GrammarType type = GrammarType.JSGF;
        final ModeType mode = ModeType.VOICE;
        final URI uri = URI.create("http://www.example.com");
        final Mrcpv2GrammarImplementation<String> grammar = 
                new Mrcpv2GrammarImplementation<String>(uri, type, mode);
        Assert.assertNull(grammar.getGrammarDocument());
     }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#getURI()}.
     */
    @Test
    public void testGetURI() {
        final URI uri = URI.create("http://www.example.com");
        final Mrcpv2GrammarImplementation<String> grammar = 
                new Mrcpv2GrammarImplementation<String>(uri, null, null);
        Assert.assertEquals(uri, grammar.getURI());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#equals(org.jvoicexml.implementation.GrammarImplementation)}.
     */
    @Test
    public void testEqualsGrammarImplementationOfT() {
        final GrammarType type = GrammarType.JSGF;
        final ModeType mode = ModeType.VOICE;
        final URI uri = URI.create("http://www.example.com");
        final Mrcpv2GrammarImplementation<String> grammar1 = 
                new Mrcpv2GrammarImplementation<String>(uri, type, mode);
        final Mrcpv2GrammarImplementation<String> grammar2 = 
                new Mrcpv2GrammarImplementation<String>(uri, type, mode);
        Assert.assertTrue(grammar1.equals(grammar2));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#equals(org.jvoicexml.implementation.GrammarImplementation)}.
     */
    @Test
    public void testEqualsGrammarImplementationOfTOtherUri() {
        final GrammarType type = GrammarType.JSGF;
        final ModeType mode = ModeType.VOICE;
        final URI uri1 = URI.create("http://www.example.com");
        final Mrcpv2GrammarImplementation<String> grammar1 = 
                new Mrcpv2GrammarImplementation<String>(uri1, type, mode);
        final URI uri2 = URI.create("http://www.example.com/other");
        final Mrcpv2GrammarImplementation<String> grammar2 = 
                new Mrcpv2GrammarImplementation<String>(uri2, type, mode);
        Assert.assertFalse(grammar1.equals(grammar2));
    }
    
    
    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2GrammarImplementation#equals(org.jvoicexml.implementation.GrammarImplementation)}.
     */
    @Test
    public void testEqualsGrammarImplementationOfTNull() {
        final GrammarType type = GrammarType.JSGF;
        final ModeType mode = ModeType.VOICE;
        final URI uri = URI.create("http://www.example.com");
        final Mrcpv2GrammarImplementation<String> grammar = 
                new Mrcpv2GrammarImplementation<String>(uri, type, mode);
        Assert.assertFalse(grammar.equals(null));
    }
}
