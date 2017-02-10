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
public final class PressDigitScript implements Script {
    /** Id of this test case. */
    private String id;
    
    /** The DTMF digit to press. */
    private String dtmf;

    /** Number of times the DTMF has to be entered. */
    private int presses;

    /**
     * Constructs a new object.
     */
    public PressDigitScript() {
        dtmf = "1";
        presses = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestId(final String testId) {
        id = testId;
    }

    /**
     * Sets the DTMF value to press.
     * @param value DTMF value to press
     */
    public void setDtmf(final String value) {
        dtmf = value;
    }

    /**
     * Sets the number how often the DTMF value should be entered.
     * @param value number of presses
     */
    public void setPresses(final int value) {
        presses = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(final Call call) {
        call.hears("Press '" + dtmf + "'.", Script.DEFAULT_TIMEOUT);
        call.enter(dtmf, DefaultScript.DEFAULT_TIMEOUT);
        if (presses > 1) {
            call.hears("Press '" + dtmf + "' again.", Script.DEFAULT_TIMEOUT);
            call.enter(dtmf);
        }
        if (presses > 2) {
            call.hears("Press '" + dtmf + "' one more time.",
                    Script.DEFAULT_TIMEOUT);
            call.enter(dtmf, DefaultScript.DEFAULT_TIMEOUT);
        }
        call.hears("assertion number " + id + ", pass", Script.DEFAULT_TIMEOUT);
    }
}
