/**
 * 
 */
package org.jvoicexml.implementation.mrcpv2;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Test class for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2SpokenInput}.
 */
public class Mrcpv2SpokenInputTest {

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2SpokenInput#getSupportedBargeInTypes()}.
     */
    @Test
    public void testGetSupportedBargeInTypes() {
        final Mrcpv2SpokenInput input = new Mrcpv2SpokenInput();
        final Collection<BargeInType> types = input.getSupportedBargeInTypes();
        Assert.assertNotNull(types);
        Assert.assertTrue(types.contains(BargeInType.HOTWORD));
        Assert.assertTrue(types.contains(BargeInType.SPEECH));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2SpokenInput#loadGrammar(java.net.URI, org.jvoicexml.xml.srgs.GrammarType)}.
     * @throws IOException test failed
     * @throws NoresourceError test failed
     * @throws UnsupportedFormatError test failed
     */
    @Test
    public void testLoadGrammarJSGF() 
            throws UnsupportedFormatError, NoresourceError, IOException {
        final Mrcpv2SpokenInput input = new Mrcpv2SpokenInput();
        final List<GrammarType> grammarTypes = 
                new java.util.ArrayList<GrammarType>();
        grammarTypes.add(GrammarType.JSGF);
        grammarTypes.add(GrammarType.SRGS_XML);
        final URI uri = URI.create("http://www.example.com");
        input.setGrammarTypes(grammarTypes);
        final GrammarImplementation<?> grammar =
                input.loadGrammar(uri, GrammarType.JSGF);
        Assert.assertNotNull(grammar);
        Assert.assertEquals(uri, grammar.getURI());
        Assert.assertEquals(GrammarType.JSGF, grammar.getMediaType());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2SpokenInput#loadGrammar(java.net.URI, org.jvoicexml.xml.srgs.GrammarType)}.
     * @throws IOException test failed
     * @throws NoresourceError test failed
     * @throws UnsupportedFormatError test failed
     */
    @Test(expected = UnsupportedFormatError.class)
    public void testLoadGrammarUnsupported() 
            throws UnsupportedFormatError, NoresourceError, IOException {
        final Mrcpv2SpokenInput input = new Mrcpv2SpokenInput();
        final List<GrammarType> grammarTypes = 
                new java.util.ArrayList<GrammarType>();
        grammarTypes.add(GrammarType.JSGF);
        grammarTypes.add(GrammarType.SRGS_XML);
        final URI uri = URI.create("http://www.example.com");
        input.setGrammarTypes(grammarTypes);
        input.loadGrammar(uri, GrammarType.SRGS_ABNF);
        Assert.fail("UnsupportedFormatError expected");
    }
}
