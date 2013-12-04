package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Script;
import org.jvoicexml.voicexmlunit.Call;

/**
 * The default implementation of a script that simply listens for
 * <em>pass</em>.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class DefaultScript implements Script {
    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(final Call call) {
        call.hears("pass", Script.DEFAULT_TIMEOUT);
    }

}
