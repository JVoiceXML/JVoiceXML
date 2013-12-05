package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Script;
import org.jvoicexml.voicexmlunit.Call;

/**
 * The default implementation of a script that listens for a request to
 * enter 1 and then presses the DTMF '1' key.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Press1Script implements Script {
    /** Id of this test case. */
    private String id;

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
        call.hears("Press '1'.", Script.DEFAULT_TIMEOUT);
        call.enter("1");
        call.hears("assertion number " + id
                + ", pass", Script.DEFAULT_TIMEOUT);
    }
}
