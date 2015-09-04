package org.jvoicexml.srgs.sisr;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface ExecutableSI {
    public void dump(String pad);

    public void execute(Context context, Scriptable scope);
}
