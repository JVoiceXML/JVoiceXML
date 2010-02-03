/**
 * 
 */
package org.jvoicexml.test.implementation;

import com.sun.speech.engine.recognition.BaseRuleGrammar;

/**
 * Basic rule grammar for testing.
 * @author Dirk Schnelle-Walka
 */
@SuppressWarnings("serial")
public final class DummyRuleGrammar extends BaseRuleGrammar {
    /**
     * Constructs a new object.
     */
    public DummyRuleGrammar() {
        super(null, "dummy", null);
    }

}
