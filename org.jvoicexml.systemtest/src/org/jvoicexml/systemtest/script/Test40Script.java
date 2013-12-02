package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Script;
import org.jvoicexml.voicexmlunit.Call;

public final class Test40Script implements Script {

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(final Call call) {
        call.hears("pass");
    }

}
