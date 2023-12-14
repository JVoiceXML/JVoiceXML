/**
 * 
 */
package org.jvoicexml.implementation.mrcpv2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.speechforge.cairo.client.SessionManager;

/**
 * Test cases for {@link Mrcpv2SpokenInputFactory}.
 * @author Dirk Schnelle-Walka
 */
public class Mrcpv2SpokenInputFactoryTest {

    /**
     * Test method for {@link org.jvoicexml.implementation.mrcpv2.Mrcpv2SpokenInputFactory#createResource()}.
     * @throws NoresourceError test failed
     */
    @Test
    public void testCreateResource() throws NoresourceError {
        final Mrcpv2SpokenInputFactory factory = new Mrcpv2SpokenInputFactory();
        factory.setInstances(1);
        factory.setBasePort(10000);
        factory.setSessionManager(new SessionManager());
        final List<GrammarType> grammarTypes = 
                new java.util.ArrayList<GrammarType>();
        grammarTypes.add(GrammarType.JSGF);
        grammarTypes.add(GrammarType.SRGS_XML);
        factory.setGrammarTypes(grammarTypes);
        final Mrcpv2SpokenInput input = 
                (Mrcpv2SpokenInput) factory.createResource();
        Assert.assertNotNull(input);
        Assert.assertEquals(grammarTypes, input.getSupportedGrammarTypes());
    }

}
