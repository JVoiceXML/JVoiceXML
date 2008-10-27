package org.jvoicexml.systemtest;

import org.jvoicexml.systemtest.response.Script;
import org.jvoicexml.systemtest.response.WaitAction;
import org.jvoicexml.systemtest.testcase.IRTestCase;

public class ScriptFactory {

    public Script create(IRTestCase testcase) {
        Script s = new Script(testcase.getId());
        s.addAction(new WaitAction());
        return s;
    }

}
