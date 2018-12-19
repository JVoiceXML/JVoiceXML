package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Script;
import org.jvoicexml.voicexmlunit.Call;

/**
 * The default implementation of a script that listens for a request to
 * enter a digit and then presses the corresponding DTMF key.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public final class EnterAssertionIdScript implements Script {
    /** Id of this test case. */
    private String id;
    
    /**
     * Constructs a new object.
     */
    public EnterAssertionIdScript() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestId(final String testId) {
        id = testId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(final Call call) {
        call.hears("Please say or key in the assertion number", Script.DEFAULT_TIMEOUT);
        call.enter(id, DefaultScript.DEFAULT_TIMEOUT);
        call.hears("assertion number " + id + ", pass", Script.DEFAULT_TIMEOUT);
    }
}
