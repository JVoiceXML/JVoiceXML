package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Script;
import org.jvoicexml.voicexmlunit.Call;

public final class Test40Script implements Script {
    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(final Call call) {
        call.hears("pass", Script.DEFAULT_TIMEOUT);
    }

}
